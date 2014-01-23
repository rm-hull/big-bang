(ns big-bang.event-handler
  (:require
    [cljs.core.async :refer [<! >! chan close! timeout]]
    [big-bang.protocol :refer [data-channel shutdown!]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn add-event-listener [element event-type]
  (let [ch (chan)
        handler (fn [event] (go (>! ch event)))]
    (.addEventListener element (name event-type) handler)
    (reify
      big-bang.protocol/IChannelSource
      (data-channel [this] ch)

      (shutdown! [this]
        (close! ch)
        (.removeEventListener element handler)))))

(defn prevent-default [event]
  (.preventDefault event))

(defn client-coords [event]
  [(.-clientX event) (.-clientY event)])

(defn coords [event]
  [(.-x event) (.-y event)])

(defn which [event]
  (.-which event))





(defn demo []
  (let [listener (add-event-listener (.-body js/document) :click)]
    (go
      (loop []
        (when-let [e (<! (data-channel listener))]
          (println "recv: " (coords e))
          (recur))))

    (go
      (<! (timeout 20000))
      (shutdown! listener)
      (println "Timed out"))))