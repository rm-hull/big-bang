(ns big-bang.examples.pacman.level-builder
  (:require [cljs.core.async :refer [chan <! map<] :as async]
            [clojure.string :refer [split-lines]]
            [dataview.loader :refer [fetch-image fetch-text]]
            [dataview.ops :refer [eod? read-delimited-string create-reader]]
            [big-bang.core :refer [big-bang!]]
            [big-bang.timer :refer [interval-ticker stop!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(set! *print-fn* (fn [s] (.log js/console s)))

(defn proxy-request [url]
  (str
    "http://programming-enchiladas.destructuring-bind.org/proxy?url="
    (js/encodeURI url)))

(def sprites "https://raw.github.com/rm-hull/big-bang/master/examples/pacman/data/spritemap-192.png")

(def level-1 "https://raw.github.com/rm-hull/big-bang/master/examples/pacman/data/1.txt")

(def canvas (.getElementById js/document "canvas"))

(def ctx (.getContext canvas "2d"))

(def cell-size 12)
(def width 29) ; 28 + newline
(def height 31)

(def canvas-coords
  (for [y (range 0 height)
        x (range 0 width)]
    [(* x cell-size) (* y cell-size)]))

(def pieces
  { "\u250F" {:x (* cell-size  4) :y (* cell-size 4) :w cell-size :h cell-size} ; ┏
    "\u2501" {:x (* cell-size  0) :y (* cell-size 4) :w cell-size :h cell-size} ; ━
    "\u2513" {:x (* cell-size  5) :y (* cell-size 4) :w cell-size :h cell-size} ; ┓
    "\u2517" {:x (* cell-size  2) :y (* cell-size 4) :w cell-size :h cell-size} ; ┗
    "\u251B" {:x (* cell-size  3) :y (* cell-size 4) :w cell-size :h cell-size} ; ┛
    "\u2503" {:x (* cell-size  1) :y (* cell-size 4) :w cell-size :h cell-size} ; ┃
    "."      {:x (* cell-size  8) :y (* cell-size 0) :w (/ cell-size 2) :h (/ cell-size 2) :target-offset (/ cell-size 4)}
    "O"      {:x (* cell-size 10) :y (* cell-size 0) :w (/ cell-size 2) :h (/ cell-size 2) :target-offset (/ cell-size 4)}})

(defn convert-cell [[dx dy] map-piece]
  (let [{:keys [x y w h target-offset]} (pieces map-piece)]
    [x y w h (+ dx target-offset) (+ dy target-offset) w h]))

(defn convert-level
  "Converts a stream of unicode characters representing a level into
   a data structure indicating where the cell is to be drawn on a
   target canvas, and the source that will be drawn."
  [raw-level-data]
  (map
    convert-cell
    canvas-coords
    (seq raw-level-data)))

(defn fetch-level [url]
  (async/map<
    convert-level
    (fetch-text url)))

(defn build-image [ctx level sprite]
  (if (empty? level)
    ctx
    (when-let [[sx sy sw sh dx dy dw dh] (first level)]
      (.drawImage ctx sprite sx sy sw sh dx dy dw dh)
      (recur ctx (rest level) sprite))))

(defn create-background-image [ctx level-chan sprite-chan]
  (async/map (partial build-image ctx) [level-chan sprite-chan]))

(defn demo[]
  (go
    ;(let [c (fetch-level (proxy-request level-1))]
    ;  (println (<! c))
    ;  (println (<! c)))

    (println
      (<! (create-background-image
            ctx
            (fetch-level (proxy-request level-1))
            (fetch-image (proxy-request sprites)))))))