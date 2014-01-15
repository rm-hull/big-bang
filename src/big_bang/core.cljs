(ns big-bang.core
  (:require [cljs.core.async :refer [<! >! chan timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def animation-frame
  (or (.-requestAnimationFrame js/window)
      (.-webkitRequestAnimationFrame js/window)
      (.-mozRequestAnimationFrame js/window)
      (.-oRequestAnimationFrame js/window)
      (.-msRequestAnimationFrame js/window)
      (fn [callback] (js/setTimeout callback 17))))

(defn package?
  "Checks to see if the supplied parameter is a package -- A package must
   consist of both a world-state and a message."
  [x]
  (and
   (contains? x :message)
   (contains? x :world-state)))

(defn make-package
  "Any handler may return either a world-state or a package. If an event
   handler produces a package, the content of the world-state field becomes
   the next world-state and the message field specifies what the world places
   on any defined send-channel."
  [world-state message]
  {:world-state world-state :message message})

(defn extract-world-state
  "Extracts the world-state from x if it is a package, else returns x"
  [x]
  (if (package? x)
    (:world-state x)
    x))

(defn extract-message
  "Extracts the message from x if it is a package, else returns nil"
  [x]
  (:message x))

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
        stop-when? (or stop-when? (constantly false))
        limit-reached? (if frame-limit #(> % frame-limit) (constantly false))
        tick-rate (or tick-rate 17)] ; 17ms = approx 58.82 FPS
    (go
      (loop [world-state initial-state
             history     []
             frame       0]
        (<! (timeout tick-rate))
        (if (or (limit-reached? frame) (stop-when? world-state))
          history
          (let [handler-result (on-tick world-state)
                next-world-state (extract-world-state handler-result)
                message (extract-message handler-result)]
            (animation-frame (fn [] (to-draw world-state)))
            (send-message send-channel message)
            (recur
              (extract-world-state next-world-state)
              (history-builder next-world-state)
              (inc frame))))))))