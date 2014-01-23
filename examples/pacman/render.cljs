(ns big-bang.examples.pacman.render
  (:require
    [cljs.core.async :refer [<!] :as async]
    [big-bang.examples.pacman.config :refer [sprites ctx background-size]]
    [big-bang.examples.pacman.util :refer [with]]
    [big-bang.examples.pacman.sprite :refer [spritemap-position]]
    [big-bang.examples.pacman.level-builder :refer [get-background]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def draw-backdrop!
  (let [[width height] (map dec background-size)]
    (fn [world-state]
      (go
        (let [backdrop (.-canvas (<! (get-background (get-in world-state [:level :number]))))]
          (.clearRect ctx 0 0 width height)
          (.drawImage ctx backdrop 0 0))))))

(defn draw-pills! [world-state]
  nil) ; TODO

(defn draw-ghosts! [world-state]
  nil) ; TODO

(defn draw-pacman! [world-state]
  (go
    (let [spritemap (<! sprites)
          [sx sy sw sh] (spritemap-position
                          [:pacman (:direction world-state)]
                          (quot (:frame world-state) 5))
          [dx dy] (:position world-state)]
      (.drawImage ctx spritemap sx sy sw sh dx dy sw sh))))

(defn draw-score! [world-state]
  nil) ; TODO

(defn render-frame [world-state]
  (go
    (with world-state
      draw-backdrop!
      draw-pills!
      draw-ghosts!
      draw-pacman!
      draw-score!)))