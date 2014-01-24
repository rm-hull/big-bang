(ns big-bang.examples.cat
  (:require [cljs.core.async :refer [chan timeout <!]]
            [dataview.loader :refer [fetch-image]]
            [big-bang.core :refer [big-bang!]]
            [big-bang.timer :refer [interval-ticker stop!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn proxy-request [url]
  (str
    "http://programming-enchiladas.destructuring-bind.org/proxy?url="
    (js/encodeURI url)))


(defn increment-and-wrap [x limit]
  (if (< x limit)
    (inc x)
    0))

(defn update-state [[x y]]
  [(increment-and-wrap x 800) y])

(defn render-scene [ctx cat-image [x y]]
  (.clearRect ctx 0 0 800 600)
  (.drawImage ctx cat-image x y))

(defn demo []
  (let [cat "https://gist.github.com/rm-hull/8859515c9dce89935ac2/raw/cat_08.jpg"
        canvas (.getElementById js/document "cat-canvas")
        ctx (.getContext canvas "2d")]
    (go
      (let [img (<! (fetch-image (proxy-request cat)))]
        (big-bang!
          :initial-state [0 0]
          :on-tick update-state
          :to-draw (partial render-scene ctx img)))))

  (def ticker (interval-ticker 17))

  (go
    (loop []
      (when-let [x (<! (:timer-chan ticker))]
        (.log js/console (str "Received: " x ))
        (recur))))

  (go
    (<! (timeout 2000)) ; pause for a short time
    (stop! ticker)))