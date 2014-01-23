(ns big-bang.protocol)

(defprotocol IChannelSource
  (data-channel [this])
  (shutdown! [this]))