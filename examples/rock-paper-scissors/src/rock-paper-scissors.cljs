(ns big-bang.examples.rock-paper-scissors
  (:require
    [cljs.core.async :refer [<! chan mult tap] :as async]
    [clojure.string :as str]
    [dommy.core :refer [insert-after!]]
    [dommy.template :as template]
    [big-bang.core :refer [big-bang!]]
    [big-bang.package :refer [make-package]]
    [dataview.loader :refer [fetch-text]]
    [big-bang.examples.rock-paper-scissors.component.human :as human]
    [big-bang.examples.rock-paper-scissors.component.opponent :as opponent]
    [big-bang.examples.rock-paper-scissors.component.referee :as referee])
  (:require-macros
    [cljs.core.async.macros :refer [go]]
    [dommy.macros :refer [sel1 sel node]]))

(enable-console-print!)

(defn proxy-request [url]
  (str
    "http://programming-enchiladas.destructuring-bind.org/proxy?url="
    (js/encodeURI url)))

(def url-root "https://raw.github.com/rm-hull/big-bang/master/examples/rock-paper-scissors/")

;(def app (sel1 :#app))

(defn style [& styles ]
  [:style (str/join \newline styles)])

(defn player-div [id title-text svg]
  [:div.aside
   [:div.title [:h3 {:id (name id)} title-text]]
   [:div.graphic
     (template/html->nodes svg)]])

(defn init-play-area [human-svg opponent-svg]
  (->>
   (sel1 :#canvas-area)
   (insert-after!
     (node
       [:div#app
         (style
           "#app { font-family: monospace; }"
           "div.pull-right { float: right; width: 150px; }"
           ".pull-right p { margin: 5px 0 }"
           "div.aside { float:left; width: 450px; padding: 20px; }"
           "#discourse-area { width: 900px; border: 1px grey solid; padding: 10px; }")
         [:div#discourse-area
           [:div.pull-right [:p#winner][:p#score]]
           [:h2 "Let's play a game - select your weapon"]]
         (player-div :human "Choose:" human-svg)
         (player-div :opponent "Your opponent is waiting..." opponent-svg)]))))

;(take 5 (make-rand-seq 0.134543543))

(defn start-game [seed]
  (let [results-chan (chan)
        notify-chan  (chan)
        m            (mult notify-chan)
        notifos (fn [] (let [c (chan)] (tap m c) c))]
    (go
      (init-play-area
        (<! (fetch-text (proxy-request (str url-root "rps.svg"))))
        (<! (fetch-text (proxy-request (str url-root "vc.svg")))))

      ; referee
      (big-bang!
        :initial-state referee/initial-state
        :to-draw referee/render
        :on-receive referee/incoming
        :receive-channel results-chan
        :send-channel notify-chan)

      ; opponent
      (big-bang!
        :initial-state (opponent/initial-state seed)
        :to-draw opponent/render
        :on-receive opponent/incoming
        :receive-channel (notifos)
        :send-channel results-chan)

      ; human player
      (big-bang!
        :initial-state human/initial-state
        :to-draw human/render
        :on-click human/update
        :event-target (sel :g.clickable)
        :on-receive human/incoming
        :receive-channel (notifos)
        :send-channel results-chan
       ))))
