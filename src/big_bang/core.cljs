(ns big-bang.core
  (:require
    [cljs.core.async :refer [<! >! chan alts!]]
    [big-bang.protocol :refer [data-channel shutdown! no-op wrap-channel]]
    [big-bang.package :refer [package? extract-message extract-world-state]]
    [big-bang.timer :refer [interval-ticker]]
    [big-bang.events.handler :refer [add-event-listener]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def animation-frame
  (or (.-requestAnimationFrame js/window)
      (.-webkitRequestAnimationFrame js/window)
      (.-mozRequestAnimationFrame js/window)
      (.-oRequestAnimationFrame js/window)
      (.-msRequestAnimationFrame js/window)
      (fn [callback] (js/setTimeout callback 17))))

(defn send-message [chan msg]
  (when (and chan msg)
    (go (>! chan msg))))

(defn make-event-source [handler event-target event-type]
  (if handler
    (add-event-listener event-target :event-type (keyword event-type))
    (no-op)))

(defn make-receive-source [handler receive-channel]
  (if (and handler receive-channel)
    (wrap-channel receive-channel)
    (no-op)))

(defn make-timer-source [handler & [interval-millis]]
  (if handler
    (interval-ticker (or interval-millis 17)) ; = approx 58.82 FPS
    (no-op)))

(defn- as-list [element-or-coll]
  (if (or (seq? element-or-coll) (array? element-or-coll))
    element-or-coll
    (list element-or-coll)))

(defn build-event-sources [{:keys [event-target
                                   on-tick tick-rate
                                   on-receive receive-channel] :as opts}]
  (let [event-target (or event-target js/document.body)
        reserved-handler-names #{:on-tick :on-receive}]
    (concat
      [{:event-name "tick"    :event-source (make-timer-source on-tick tick-rate) :handler on-tick}
       {:event-name "receive" :event-source (make-receive-source on-receive receive-channel) :handler on-receive}]
      (for [[k v] opts
            :let  [[_ event-type] (re-matches #"on-(.*)" (name k))]
            :when (and event-type (nil? (reserved-handler-names k)))
            target (as-list event-target)]
        {:event-name event-type :event-source (make-event-source v target event-type) :handler v}))))

(defn shutdown-all [handlers]
  (doseq [handler handlers]
    (shutdown! handler)))

(defn build-dispatch-table [event-sources]
  (into {}
    (map
      #(vector (data-channel (:event-source %)) %)
      event-sources)))

(defn big-bang!
  "Loosely based on Racket's big-bang, but executes in a go-block."
  [& {:keys [initial-state stop-when? max-frames to-draw
             record? playback
             event-target
             on-tick tick-rate
             on-receive receive-channel send-channel] :as opts}]
  (let [history-builder (if record? conj (constantly nil))
        limit-reached? (if max-frames #(> % max-frames) (constantly false))
        stop-when? (or stop-when? (constantly false))
        dispatch-table (build-dispatch-table (build-event-sources opts))
        ports (keys dispatch-table)]

    (go
      (loop [world-state initial-state
             history     []
             frame       0]
        (let [[value port] (alts! ports)
              handler (get-in dispatch-table [port :handler])
              handler-result (handler value world-state)
              next-world-state (extract-world-state handler-result)
              message (extract-message handler-result)]
          (send-message send-channel message)
          (if (or (limit-reached? frame) (stop-when? next-world-state))

            ; initiate shutdown
            (do
              (shutdown-all (map :event-source (vals dispatch-table)))
              history) ; TODO: this won't return to the original caller.. need to think about that

            ; keep on truckin'
            (do
              (when (not= world-state next-world-state)
                (animation-frame
                  (fn [] (to-draw next-world-state))))

              (recur
                next-world-state
                (history-builder history next-world-state)
                (inc frame)))))))))
