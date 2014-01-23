(ns big-bang.examples.pacman.util
  (:require [cljs.core.async :refer [<! >! chan close!] :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(set! *print-fn* (fn [s] (.log js/console s)))

(defn posn [x y sz]
  {:x (* sz x) :y (* sz y) :w sz :h sz})

(defn into-channel
  "Pours the contents of the collection into a channel. A bit like
  core.async/to-chan, but slightly different in some way I can't
  yet explain."
  [chan coll]
  (go
    (loop [xs coll]
      (if (empty? xs)
        (close! chan)
        (do
          (>! chan (first xs))
          (recur (rest xs)))))))

(defn proxy-request [url]
  (str
    "http://programming-enchiladas.destructuring-bind.org/proxy?url="
    (js/encodeURI url)))

(defn with [arg & fns]
  (doseq [fn fns]
    (fn arg)))