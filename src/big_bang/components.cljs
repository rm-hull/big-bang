(ns big-bang.components
  (:require-macros
    [cljs.core.async.macros :refer [go]]
    [dommy.macros :refer [node]])
  (:require
    [cljs.core.async :refer [>!]]
    [dommy.core :refer [set-html!]]
    [big-bang.core :refer [big-bang]]
    [big-bang.events.browser :refer [target]]
    [big-bang.package :refer [make-package]]))

(defn dropdown
  "Returns a labelled HTML select bound to a channel"
  [& {:keys [id label-text options initial-value send-channel]}]
  (let [id-str (name id)
        element (node
                  [:select {:id id-str :name id-str}
                    (for [[k v] (sort-by second options)]
                      [:option
                       (if (= k initial-value)
                                 {:value k :selected "selected"}
                                 {:value k })
                       v])])]
    (big-bang
      :initial-state {id initial-value}
      :send-channel send-channel
      :event-target element

      :on-change (fn [event world-state]
                   (let [value (-> event target .-value)]
                     (make-package value {id value}))))

    (go (>! send-channel {id initial-value}))

    (node
     [:span.big-bang-component.dropdown
       [:label {:for id-str} label-text]
       element])))

(defn slider
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
     [:span.big-bang-component.slider
       [:label {:for (name id)} label-text]
       input-element
       output-element])))

(defn color-picker
  "Returns a labelled HTML5 color-picker input bound to a channel"
  [& {:keys [id label-text options initial-value send-channel]}]
  (let [id-str (name id)
        element (node
                  [:input {:id id-str
                           :name id-str
                           :type "color" }])]

    (set! (.-value element) initial-value)

    (big-bang
      :initial-state {id initial-value}
      :send-channel send-channel
      :event-target element

      :on-change (fn [event world-state]
                   (let [value (-> event target .-value)]
                     (make-package value {id value}))))

    (go (>! send-channel {id initial-value}))

    (node
     [:span.big-bang-component.color-picker
       [:label {:for id-str} label-text]
       element])))
