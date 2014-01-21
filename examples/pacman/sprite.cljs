(ns big-bang.examples.pacman.sprite
  (:require [cljs.core.async :refer [chan <! map<] :as async]
            [big-bang.examples.pacman.config :refer [cell-size] :as config]
            [big-bang.examples.pacman.util :refer [posn into-channel]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- frames [& frame-refs]
  (go (into-channel (cycle frame-refs))))

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