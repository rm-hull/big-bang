(ns big-bang.examples.rock-paper-scissors.component.human
  (:require
    [clojure.string :as str]
    [dommy.core :refer [attr set-html!]]
    [big-bang.events.browser :refer [target]]
    [big-bang.package :refer [make-package]])
  (:require-macros
    [dommy.macros :refer [sel1]]))

(def initial-state {:id :human})

(defn update [event world-state]
  (let [chosen-weapon (-> event target .-parentNode (attr "data-type") keyword)]
    (make-package
      (assoc world-state :weapon chosen-weapon)   ; new world-state
      {:from :human :weapon chosen-weapon}))) ; message

(defn incoming [event world-state]
  world-state)

(defn render [world-state]
  (when-let [weapon (:weapon world-state)]
    (->
     (sel1 :#human)
     (set-html! (str "You chose: " (-> weapon name str/upper-case))))))
