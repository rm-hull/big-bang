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