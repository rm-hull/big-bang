(ns big-bang.event-handler
  (:require
    [cljs.core.async :refer [<! >! chan close! timeout]]
    [big-bang.protocol :refer [data-channel shutdown!]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn add-event-listener
  "Binds an event-type listener to the element. The event-type may be specified
   as a string or keyword, but can match the published onkey.../onmouse.../etc
   or custom events posted to components.

   Returns a reified IChannelSource which exposes the delivery channel on to
   events are posted, and a mechanism to shutdown and de-install the event
   listener."
  [element event-type]
  (let [ch (chan)
        handler (fn [event] (go (>! ch event)))]
    (.addEventListener element (name event-type) handler)
    (reify
      big-bang.protocol/IChannelSource
      (data-channel [this] ch)

      (shutdown! [this]
        (close! ch)
        (.removeEventListener element handler)))))

(defn prevent-default
  "If an event is cancelable, this function is used to signify that the event
   is to be cancelled, meaning any default action normally taken by the
   implementation as a result of the event will not occur."
  [event]
  (.preventDefault event))

(defn stop-propagation
  "This function is used prevent further propagation of an event during
   event flow."
  [event]
  (.stopPropagation event))

(defn client-coords
  "(x,y) co-ordinates at which the event occurred relative to the
   DOM implementation's client area."
  [event]
  [(.-clientX event) (.-clientY event)])

(defn coords
  "(x,y) co-ordinates at which the event occurred relative to the
   origin of the screen coordinate system."
  [event]
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