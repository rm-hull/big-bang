(ns big-bang.examples.pacman.level-builder
  (:require [cljs.core.async :refer [chan <! map<] :as async]
            [clojure.string :refer [split-lines]]
            [dataview.ops :refer [create-reader]]
            [big-bang.examples.pacman.config :as config]
            [big-bang.examples.pacman.util :refer [posn into-channel]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def pieces
  { "\u250F" (posn 4 4 config/cell-size) ; ┏
    "\u2501" (posn 0 4 config/cell-size) ; ━
    "\u2513" (posn 5 4 config/cell-size) ; ┓
    "\u2517" (posn 2 4 config/cell-size) ; ┗
    "\u251B" (posn 3 4 config/cell-size) ; ┛
    "\u2503" (posn 1 4 config/cell-size) ; ┃
    ; TODO below bits to be removed - pills to draw separately
    "."      (assoc (posn 16 0 (/ config/cell-size 2)) :target-offset (/ config/cell-size 4))
    "O"      (assoc (posn 20 0 (/ config/cell-size 2)) :target-offset (/ config/cell-size 4))})

(defn- make-directive
  "Takes target co-ordinates and a map-piece character. The layout directive
   for the map-piece is looked up and combined with the co-ords. The return
   value is a vector suitable for applying directly to ctx.drawImage"
  [[dx dy] map-piece]
  (let [{:keys [x y w h target-offset]} (pieces map-piece)]
    [x y w h (+ dx target-offset) (+ dy target-offset) w h]))

(defn- convert-level
  "Converts a stream of unicode characters representing a level into
   a data structure indicating where the cell is to be drawn on a
   target canvas, and the source that will be drawn."
  [raw-level-data]
  (let [canvas-coords (for [y (range 0 config/height)
                            x (range 0 config/width)]
                        (map (partial * config/cell-size) [x y]))]
    (map
      make-directive
      canvas-coords
      (seq raw-level-data))))

(defn- make-ctx
  "Creates a CanvasRenderingContext2D with the given width and height."
  [[width height]]
  (let [canvas (.createElement js/document "canvas")]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    (.getContext canvas "2d")))

(defn- draw-cells
  "Draws cells from the sprite-map onto the given context according
  to the layout directives in the level. The context is returned
  (for threading)."
  [ctx level sprite-map]
  (loop [level level]
    (if (empty? level)
      ctx
      (when-let [[sx sy sw sh dx dy dw dh] (first level)]
        (.drawImage ctx sprite-map sx sy sw sh dx dy dw dh)
        (recur (rest level))))))

(defn- create-background-image [level-chan sprite-chan]
  (async/map
    (partial draw-cells (make-ctx config/background-size))
    [level-chan sprite-chan]))

(def get-background
  (memoize
    (fn [n]
      (let [c (chan)]
        (go (into-channel c
              (repeat (<! (create-background-image
                            (async/take 1 (async/map< convert-level (config/level n)))
                            (async/take 1 config/sprites))))))
        c))))