(ns big-bang.core
  (:require [cljs.core.async :refer [chan]])
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

(defn big-bang!
  "Loosely based on Racket's big-bang"
  [& {:keys [initial-state on-tick to-draw stop-when on-key on-mouse]}]
  ; :on-key and :on-mouse are presently not implemented
  (let [stop-when (or stop-when (constantly false))]
    (letfn [(loop [state]
              (fn []
                (when-not (stop-when state)
                  (animation-frame (loop (on-tick state)))
                  (to-draw state))))]
      ((loop initial-state)))))