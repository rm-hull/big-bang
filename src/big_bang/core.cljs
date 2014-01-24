(ns big-bang.core
  (:require
    [cljs.core.async :refer [<! >! chan]]
    [big-bang.protocol :refer [data-channel shutdown! no-op]]
    [big-bang.package :refer [package? extract-message extract-world-state]]
    [big-bang.timer :refer [interval-ticker]]
    [big-bang.event-handler :refer [add-event-listener]])
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
    (>! chan msg)))

(defn big-bang!
  "Loosely based on Racket's big-bang, but executes in a go-block."
  [& {:keys [initial-state stop-when? frame-limit to-draw
             on-tick tick-rate record? playback
             on-key on-release on-mouse
             on-receive receive-channel send-channel]}]
  (let [history-builder (if record? conj (constantly nil))
        limit-reached? (if frame-limit #(> % frame-limit) (constantly false))
        stop-when? (or stop-when? (constantly false))
        ticker (interval-ticker (or tick-rate 17)) ; 17ms = approx 58.82 FPS
        keydown-handler (if on-key
                          (add-event-listener (.-body js/document) :keydown)
                          (no-op))
        mouse-handler (if on-mouse
                          (add-event-listener (.-body js/document) :click)
                          (no-op))]
    (go
      (loop [world-state initial-state
             history     []
             frame       0]
        (let [handler-result (alt!
                               (data-channel keydown-handler) ([event ch] (on-key event world-state))
                               (data-channel mouse-handler)   ([event ch] (on-mouse event world-state))
                               (data-channel ticker)          ([value ch] (on-tick world-state)))
              next-world-state (extract-world-state handler-result)
              message (extract-message handler-result)]
          (send-message send-channel message)
          (if (or (limit-reached? frame) (stop-when? next-world-state))

            ; initiate shutdown
            (do
              (shutdown! ticker)
              (shutdown! keydown-handler)
              history)

            ; keep on truckin'
            (do
              (animation-frame (fn [] (to-draw world-state)))
              (recur
                next-world-state
                (history-builder history next-world-state)
                (inc frame)))))))))