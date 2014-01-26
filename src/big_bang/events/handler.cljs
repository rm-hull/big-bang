(ns big-bang.events.handler
  (:require
    [cljs.core.async :refer [<! >! chan close! timeout]]
    [big-bang.protocol :refer [data-channel shutdown!]]
    [big-bang.events.browser :refer [prevent-default]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- add-event-listener
  "Binds an event-type listener to the element. The event-type may be specified
   as a string or keyword, but can match the published onkey.../onmouse.../etc
   or custom events posted to components.

   Returns a reified IChannelSource which exposes the delivery channel on to
   which events are posted, and a mechanism to shutdown and de-install the
   event listener."
  [element & {:keys [event-type prevent-default?] :as opts}]
  (let [ch (chan)
        handler (fn [event]
                  (when prevent-default? (prevent-default event))
                  (go (>! ch event)))]
    (.addEventListener element (name event-type) handler)
    (reify
      big-bang.protocol/IChannelSource
      (data-channel [this] ch)

      (shutdown! [this]
        (close! ch)
        (.removeEventListener element handler)))))
