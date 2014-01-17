(ns big-bang.timer
  (:require [cljs.core.async :refer [<! >! close! chan dropping-buffer]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn interval-ticker
  "Creates and starts an interval timer, regularly delivering messages to a
   timer channel (accessible via :timer-chan in the returned map).

   The payload message, by default, contains the system time in milliseconds,
   but can be supplied via the second optional argument, which should be a
   0-arity function. It is assumed that it may have side-effects."
  ([msec] (interval-ticker msec #(.getTime (js/Date.))))
  ([msec payload-generator-fn]
    (let [comm-chan   (chan 1)
          timer-chan  (chan (dropping-buffer 1))
          interval-fn (fn []
                        (go
                          (if-let [interval-id (<! comm-chan)]
                            (do
                              (>! timer-chan (payload-generator-fn))
                              (>! comm-chan interval-id))
                            (close! timer-chan))))
          interval-id (js/setInterval interval-fn msec)]
      (go (>! comm-chan interval-id)) ; bootstrap via the comm-chan
      {:comm-chan comm-chan
       :timer-chan timer-chan
       :interval-id interval-id})))

(defn stop!
  "Closes the timer's comms channel which prevents further payload
   messages being emitted. The associated interval is also cancelled.
   There is no way to restart a timer once stopped."
  [{:keys [comm-chan timer-chan interval-id]}]
  (go
    (close! comm-chan)
    (js/clearInterval interval-id)))