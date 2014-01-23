(ns big-bang.examples.pacman.config
  (:require [cljs.core.async :refer [chan <!] :as async]
            [big-bang.examples.pacman.util :refer [into-channel proxy-request]]
            [dataview.loader :refer [fetch-image fetch-text]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def canvas (.getElementById js/document "pacman-canvas"))

(def ctx (when canvas (.getContext canvas "2d")))

(def cell-size 12)

(def width 29) ; 28 + newline

(def height 31)

(def background-size
  (mapv (partial * cell-size) [width height]))

(def start-position
  "Pacman's starting position"
  (mapv (partial * cell-size) [13.5 23]))

(def frighten-duration 1000) ; TODO arbitrary number alert

(def url {
  :sprite-map "https://raw.github.com/rm-hull/big-bang/master/examples/pacman/data/spritemap-192.png"
  :levels ["https://raw.github.com/rm-hull/big-bang/master/examples/pacman/data/" ".txt"]})

(def sprites (let [c (chan)]
               (go (into-channel c (repeat (<! (fetch-image (proxy-request (:sprite-map url)))))))
               c))

(def level
  (memoize
    (fn [n]
      (let [[prefix suffix] (:levels url)
            url (str prefix n suffix)
            c (chan)]
        (go (into-channel c (repeat (<! (fetch-text (proxy-request url))))))
        c))))