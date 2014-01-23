(ns big-bang.core
  (:require
    [cljs.core.async :refer [<! >! chan]]
    [big-bang.protocol :refer [data-channel shutdown!]]
    [big-bang.package :refer [package? extract-message extract-world-state]]
    [big-bang.timer :refer [interval-ticker]]
    [big-bang.event-handler :refer [add-event-listener]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

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
        ticker (interval-ticker (or tick-rate 17))] ; 17ms = approx 58.82 FPS
    (go
      (loop [world-state initial-state
             history     []
             frame       0]
        (<! (data-channel ticker))
        (if (or (limit-reached? frame) (stop-when? world-state))

          ; initiate shutdown
          (do
            (shutdown! ticker)
            history)

          ; keep on truckin'
          (let [handler-result (on-tick world-state)
                next-world-state (extract-world-state handler-result)
                message (extract-message handler-result)]
            (animation-frame (fn [] (to-draw world-state)))
            (send-message send-channel message)
            (recur
              (extract-world-state next-world-state)
              (history-builder history next-world-state)
              (inc frame))))))))