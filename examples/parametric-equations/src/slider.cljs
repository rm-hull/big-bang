(ns big-bang.examples.parametric-equations.slider
  (:require
    [dommy.core :refer [set-html!]]
    [big-bang.core :refer [big-bang]]
    [big-bang.events.browser :refer [target]]
    [big-bang.package :refer [make-package]])
  (:require-macros
    [dommy.macros :refer [node]]))

(defn make-slider
  "Returns a labelled HTML5 range slider bound to a channel"
  [& {:keys [id label-text initial-value min-value max-value step send-channel]}]
  (let [id-str (name id)
        result-id (str id-str "-result")
        input-element  (node
                         [:input
                          {:id    id-str
                           :name  id-str
                           :type  "range"
                           :min   (str (or min-value 0))
                           :max   (str (or max-value 100))
                           :value (str (or initial-value 50))
                           :step  (str (or step 1))}])
        output-element (node
                         [:output
                           {:id   result-id
                            :name result-id
                            :for  id-str}])]

    (big-bang
      :initial-state initial-value
      :send-channel send-channel
      :event-target input-element

      :to-draw (fn [world-state]
                 (set-html! output-element world-state))

      :on-change (fn [event world-state]
                   (let [value (-> event target .-valueAsNumber)]
                     (make-package value {id value}))))

    (node
     [:span.slider
       [:label {:for (name id)} label-text]
       input-element
       output-element])))
