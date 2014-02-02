(ns big-bang.examples.parametric-equations
  (:require
    [cljs.core.async :refer [<! chan mult tap] :as async]
    [clojure.string :as str]
    [dommy.core :refer [insert-after!]]
    [dommy.template :as template]
    [monet.canvas :refer [fill-style fill-rect circle rotate translate composition-operation save restore]]
    [big-bang.core :refer [big-bang]]
    [big-bang.package :refer [make-package]]
    [big-bang.examples.parametric-equations.slider :refer [make-slider]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]
    [dommy.macros :refer [sel1 sel node]]))

(enable-console-print!)

(def canvas (.getElementById js/document "canvas-area"))
(def ctx (when canvas (.getContext canvas "2d")))
(def width 800) ; 28 + newline
(def height 600)
(def initial-k 0.45)
(def initial-persistence 95)
(def canvas { :x (quot width -2) :y (quot height -2) :w width :h height})

(defn initial-state []
  {:t 0
   :k initial-k
   :ctx ctx
   :persistence initial-persistence })

(defn incoming [event world-state]
  (merge world-state event (if (:k event) {:clear? true})))

(defn tock [event world-state]
  (->
    world-state
    (update-in [:t] inc)
    (assoc :clear? false)))

(defn draw-point! [ctx t k]
  (let [t (/ t 60)
        a 1
        b (/ a k)
        a-minus-b (- a b)
        x (+ (* a-minus-b (Math/cos t))
             (* b         (Math/cos (* t (dec k)))))
        y (- (* a-minus-b (Math/sin t))
             (* b         (Math/sin (* t (dec k)))))
        scale (if (< k 1.0)
                (* 150 k)
                200)]
    (->
      ctx
      (fill-style :red)
      (circle {:x (* scale x) :y (* scale y) :r 3}))))

(defn render-frame! [{:keys [clear? k t persistence ctx] :as world-state}]
  (let [color (if clear?
                :white
                (str "rgba(255,255,255," (double (/ (- 100 persistence) 100)) ")"))]
    (->
      ctx
      (fill-style color)
      (fill-rect canvas)
      ;(composition-operation :lighter)
      (draw-point! t k))))

(defn start []

  (let [chan (chan)]
    (->>
     (sel1 :#canvas-area)
     (insert-after! (node
                     [:div
                        (make-slider
                          :id :persistence
                          :label-text "Persistence:"
                          :min-value 0
                          :max-value 100
                          :initial-value initial-persistence
                          :send-channel chan)
                        [:span {:style "width: 20px"}]
                        (make-slider
                          :id :k
                          :label-text "k:"
                          :label "k:"
                          :min-value 0
                          :max-value 10
                          :step 0.05
                          :initial-value initial-k
                          :send-channel chan)])))

    (translate ctx (quot width 2) (quot height 2))

    (big-bang
      :initial-state (initial-state)
      :receive-channel chan
      :on-receive incoming
      :on-tick tock
      :to-draw render-frame!)))
