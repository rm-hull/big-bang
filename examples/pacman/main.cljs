(ns big-bang.examples.pacman.main
  (:require
    [cljs.core.async :refer [<!] :as async]
    [big-bang.core :refer [big-bang!]]
    [big-bang.event-handler :refer [which prevent-default]]
    [big-bang.examples.pacman.config :refer [width height cell-size] :as config]
    [big-bang.examples.pacman.render :refer [make-render-frame]]
    [big-bang.examples.pacman.level-builder :refer [get-background]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

; -------------------- namespace this ----------------
(defn make-ghost []
  {}) ; TODO

(defn advance-ghost [world-state ghost-name]
  world-state) ; TODO

(defn frighten-ghost [world-state ghost-name]
  world-state) ; TODO
; ----------------------------------------------------

(defn posn->cell [[x y]]
  [(quot x cell-size) (quot y cell-size)])

(defn cell->offset [[x y]]
  (+ x (* width y)))

(defn adjust-for [direction [x y]]
  (case direction
    :right [(+ x cell-size) y]
    :down  [x (+ y cell-size)]
           [x y]))

(defn level-item [world-state next-posn]
  (->>
    next-posn
    (adjust-for (:direction world-state))
    (posn->cell)
    (cell->offset)
    (vector :level :map)
    (get-in world-state)))

(defn update-score [world-state points-to-add]
  (update-in world-state [:score] (partial + points-to-add)))

(defn eat-pill [world-state pill-pos]
  (let [pill-offset (->> pill-pos (posn->cell) (cell->offset))]
    (assoc-in world-state [:level :map pill-offset] " ")))

(defn update-position [world-state new-posn]
  (assoc-in world-state [:position] new-posn))

(defn calc-next-position [world-state]
  (let [[x y] (:position world-state)]
    (case (:direction world-state)
      :right [(inc x) y]
      :left  [(dec x) y]
      :up    [x (dec y)]
      :down  [x (inc y)])))

(def screen-wrap
  (let [[maxx maxy] (map dec config/background-size)]
    (fn [[x y]]
      [(cond
      (< x 0)    maxx
      (> x maxx) 0
      :else      x)
     (cond
      (< y 0)    maxy
      (> y maxx) 0
      :else      y)])))

(defn advance-pacman [world-state]
  (let [next-pos (-> world-state calc-next-position screen-wrap)
        new-cell (level-item world-state next-pos)]
    (condp = new-cell
      " " (->
            world-state
            (update-position next-pos))
      "." (->
            world-state
            (update-position next-pos)
            (eat-pill next-pos)
            (update-score 1)) ; also TODO: emit message for sound handling
      "O" (->
            world-state
            (update-position next-pos)
            (eat-pill next-pos)
            (update-score 10)
            (frighten-ghost :blinky)
            (frighten-ghost :pinky)
            (frighten-ghost :inky)
            (frighten-ghost :clyde)) ; TODO emit message for sound handling
      ; else do nothing: i.e. dont allow to drill through walls
            world-state)))

(defn update-state [world-state]
  (->
    world-state
    (update-in [:frame] inc)
    (advance-ghost :blinky)
    (advance-ghost :pinky)
    (advance-ghost :inky)
    (advance-ghost :clyde)
    advance-pacman))

(defn make-initial-state [level-num level-data]
  {
   :lives 3
   :frame 0
   :score 0
   :direction :left
   :position config/start-position
   :ghosts {
     :blinky (make-ghost)
     :pinky  (make-ghost)
     :inky   (make-ghost)
     :clyde  (make-ghost)}
   :level {
     :map (vec level-data)
     :number level-num
   }})

(def directions
  { 37 :left
    38 :up
    39 :right
    40 :down})

(defn change-direction [event world-state]
  (if-let [dir (directions (which event))]
    (assoc world-state :direction dir)
    world-state))

(defn start-game []
  (go
    (let [n 1
          sprite-map (<! config/sprites)
          backdrop   (.-canvas (<! (get-background n)))
          render-frame (make-render-frame
                         sprite-map
                         backdrop
                         config/background-size)]
      (big-bang!
        :initial-state (make-initial-state n (<! (config/level n)))
        :on-tick update-state
        :on-key change-direction
        :to-draw render-frame))))