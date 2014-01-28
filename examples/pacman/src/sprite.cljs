(ns big-bang.examples.pacman.sprite
  (:require [big-bang.examples.pacman.config :refer [cell-size] :as config]
            [big-bang.examples.pacman.util :refer [posn]]))

(defn- frames
  "Returns a function which given a frame number, will return the appropriate
   frame reference."
  [& frame-refs]
  (let [v (mapv vals frame-refs)
        cnt (count v)]
    (fn [frame-num]
      (v (mod frame-num cnt)))))

(def sprites {
  :pacman {
    :up    (frames
             (posn 1 3 cell-size)
             (posn 3 3 cell-size)
             (posn 0 7 cell-size)
             (posn 3 3 cell-size))
    :down  (frames
             (posn 5 3 cell-size)
             (posn 7 3 cell-size)
             (posn 0 7 cell-size)
             (posn 7 3 cell-size))
    :left  (frames
             (posn 0 3 cell-size)
             (posn 2 3 cell-size)
             (posn 0 7 cell-size)
             (posn 2 3 cell-size))
    :right (frames
             (posn 4 3 cell-size)
             (posn 6 3 cell-size)
             (posn 0 7 cell-size)
             (posn 6 3 cell-size))}
  :blinky {} ; red
  :pinky {}  ; pink
  :inky {}   ; cyan
  :clyde {}  ; orange
})

(defn spritemap-position [refs frame]
  ((get-in sprites refs) frame))