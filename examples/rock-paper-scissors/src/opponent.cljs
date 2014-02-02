(ns big-bang.examples.rock-paper-scissors.component.opponent
  (:require
    [clojure.string :as str]
    [dommy.core :refer [attr set-html!]]
    [big-bang.package :refer [make-package]])
  (:require-macros
    [dommy.macros :refer [sel1]]))

(def choices [:rock :paper :scissors])

; https://github.com/richhickey/clojure-contrib/blob/master/src/main/clojure/clojure/contrib/probabilities/random_numbers.clj
; http://en.wikipedia.org/wiki/Linear_congruential_generator
(defn make-rand-seq [seed]
  (let [m (Math/pow 2 32) ; From "Numerical Recipies"
        a 1664525
        c 1013904223]
    (letfn [(seq0 [seed]
              (let [value (/ (float seed) (float m))
                    new-seed (rem (+ c (* a seed)) m)]
                (lazy-seq (cons value (seq0 new-seed)))))]
      (seq0 seed))))

(defn initial-state [seed]
  {:id :opponent
   :rnd-seq (->>
              seed
              make-rand-seq
              (map (comp choices int (partial * 3))))})

(defn render [world-state]
  (when-let [weapon (:weapon world-state)]
    (->
     (sel1 :#opponent)
     (set-html! (str "Your opponent chose: " (-> weapon name str/upper-case))))))

(defn choose [world-state]
  (let [rnd (:rnd-seq world-state)
        chosen-weapon (first rnd)]
    (make-package
      (assoc world-state :weapon chosen-weapon :rnd-seq (rest rnd))
      {:from :opponent :weapon chosen-weapon})))

(defn incoming [event world-state]
  (condp = event
    {:to :opponent :choose true}
    (choose world-state)

    ; default
    world-state))
