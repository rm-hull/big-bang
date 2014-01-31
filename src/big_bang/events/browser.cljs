(ns big-bang.events.browser)

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

(defn target [event]
  (.-target event))
