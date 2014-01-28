(ns big-bang.examples.pacman.render
  (:require
    [cljs.core.async :refer [<!] :as async]
    [big-bang.examples.pacman.config :refer [ctx]]
    [big-bang.examples.pacman.util :refer [with]]
    [big-bang.examples.pacman.sprite :refer [spritemap-position]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn draw-backdrop! [[width height] backdrop world-state]
  (.clearRect ctx 0 0 width height)
  (.drawImage ctx backdrop 0 0))

(defn draw-pills! [world-state]
  nil) ; TODO

(defn draw-ghosts! [world-state]
  nil) ; TODO

(defn draw-pacman! [sprite-map world-state]
  (let [[sx sy sw sh] (spritemap-position
                        [:pacman (:direction world-state)]
                        (quot (:frame world-state) 5))
        [dx dy] (:position world-state)]
    (.drawImage ctx sprite-map sx sy sw sh dx dy sw sh)))

(defn draw-score! [world-state]
  nil) ; TODO

(defn make-render-frame [sprite-map backdrop background-size]
  (let [background-size (map dec background-size)
        draw-backdrop! (partial draw-backdrop! background-size backdrop)
        draw-pacman! (partial draw-pacman! sprite-map)]
    (fn [world-state]
      (with world-state
        draw-backdrop!
        draw-pills!
        draw-ghosts!
        draw-pacman!
        draw-score!))))