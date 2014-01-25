(ns big-bang.protocol
  (:require [cljs.core.async :refer [chan close!]]))

; This protocol encapsulates a channel and the ability to
; close that channel, ...erm, so why not just use a channel?
;
; For more complex applications, such as the interval-timer,
; where there are a pair of communicating channels, we need
; to orchestrate message delivery and shutting down both
; channels. Similarly, the event listener wraps a channel
; a listener and registering and deregistering against a
; DOM element.

(defprotocol IChannelSource
  (data-channel [this])
  (shutdown! [this]))

(defn no-op
  "Does nothing, so guaranteed to never deliver a message
   on the data channel."
  []
  (let [c (chan)]
    (reify
      IChannelSource
      (data-channel [this] c)
      (shutdown! [this] (close! c)))))
