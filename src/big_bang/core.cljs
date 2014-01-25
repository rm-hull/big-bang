(ns big-bang.core
  (:require
    [cljs.core.async :refer [<! >! chan]]
    [big-bang.protocol :refer [data-channel shutdown! no-op]]
    [big-bang.package :refer [package? extract-message extract-world-state]]
    [big-bang.timer :refer [interval-ticker]]
    [big-bang.event-handler :refer [add-event-listener wrap-channel]])
  (:require-macros
    [cljs.core.async.macros :refer [go alt!]]))

(def animation-frame
  (or (.-requestAnimationFrame js/window)
      (.-webkitRequestAnimationFrame js/window)
      (.-mozRequestAnimationFrame js/window)
      (.-oRequestAnimationFrame js/window)
      (.-msRequestAnimationFrame js/window)
      (fn [callback] (js/setTimeout callback 17))))

(defn send-message [chan msg]
  (when (and chan msg)
    (go (>! chan msg))))

(defn big-bang!
  "Loosely based on Racket's big-bang, but executes in a go-block."
  [& {:keys [initial-state stop-when? max-frames to-draw
             record? playback
             event-target
             on-tick tick-rate
             on-key on-release
             on-mouseclick on-mousemove
             on-touch
             on-receive receive-channel send-channel] :as opts}]
  (let [history-builder (if record? conj (constantly nil))
        limit-reached? (if max-frames #(> % max-frames) (constantly false))
        stop-when? (or stop-when? (constantly false))
        event-target (or event-target js/document.body)

        receive-handler (if (and on-receive receive-channel)
                          (wrap-channel receive-channel)
                          (no-op))
        ticker (if on-tick
                 (interval-ticker (or tick-rate 17)) ; 17ms = approx 58.82 FPS
                 (no-op))

        ; TODO: need a generic way to auto-create handlers based on opts [easy]
        ;       and then pipe them into the alt! macro [not so easy]
        keydown-handler (if on-key
                          (add-event-listener
                            event-target
                            :event-type :keydown
                            :prevent-default? false)
                          (no-op))
        mouseclick-handler (if on-mouseclick
                             (add-event-listener
                               event-target
                               :event-type :click)
                             (no-op))
        mousemove-handler (if on-mousemove
                             (add-event-listener
                               event-target
                               :event-type :mousemove)
                             (no-op))]
    (go
      (loop [world-state initial-state
             history     []
             frame       0]
        (let [handler-result (alt!
                               (data-channel keydown-handler)    ([event ch] (on-key event world-state))
                               (data-channel mouseclick-handler) ([event ch] (on-mouseclick event world-state))
                               (data-channel mousemove-handler)  ([event ch] (on-mousemove event world-state))
                               (data-channel ticker)             ([value ch] (on-tick world-state))
                               (data-channel receive-handler)    ([value ch] (on-receive value world-state)))
              next-world-state (extract-world-state handler-result)
              message (extract-message handler-result)]
          (send-message send-channel message)
          (if (or (limit-reached? frame) (stop-when? next-world-state))

            ; initiate shutdown
            (do
              (shutdown! keydown-handler)
              (shutdown! mouseclick-handler)
              (shutdown! mousemove-handler)
              (shutdown! ticker)
              (shutdown! receive-handler)
              history) ; TODO: this won't return to the original caller.. need to think about that

            ; keep on truckin'
            (do
              (animation-frame (fn [] (to-draw world-state))) ; TODO: only draw if world-state != next-world-state
              (recur
                next-world-state
                (history-builder history next-world-state)
                (inc frame)))))))))
