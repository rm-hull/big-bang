(ns big-bang.examples.cat
  (:require [cljs.core.async :refer [chan]]
            [dataview.loader :refer [fetch-image]]
            [big-bang.core :refer [big-bang!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn proxy-request [url]
  (str
    "http://programming-enchiladas.destructuring-bind.org/proxy?url="
    (js/encodeURI url)))


(def cat "https://gist.github.com/rm-hull/8859515c9dce89935ac2/raw/cat_08.jpg")

(def canvas (.getElementById js/document "canvas"))

(def ctx (.getContext canvas "2d"))

(defn increment-and-wrap [x limit]
  (if (< x limit)
    (inc x)
    0))

(defn update-state [[x y]]
  [(increment-and-wrap x 800) y])

(defn render-scene [ctx cat-image [x y]]
  (.clearRect ctx 0 0 800 600)
  (.drawImage ctx cat-image x y))

(go
  (let [img (<! (fetch-image (proxy-request cat)))]
    (big-bang!
      :initial-state [0 300]
      :on-tick update-state
      :to-draw (partial render-scene ctx img))))

