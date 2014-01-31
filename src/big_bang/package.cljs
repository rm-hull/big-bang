(ns big-bang.package)

(defn package?
  "Checks to see if the supplied parameter is a package -- A package must
   consist of both a world-state and a message."
  [x]
  (and
   (contains? x :message)
   (contains? x :world-state)))

(defn make-package
  "Any handler may return either a world-state or a package. If an event
   handler produces a package, the content of the world-state field becomes
   the next world-state and the message field specifies what the world places
   on any defined send-channel."
  [world-state message]
  {:world-state world-state :message message})

(defn extract-world-state
  "Extracts the world-state from x if it is a package, else returns x"
  [x]
  (if (package? x)
    (:world-state x)
    x))

(defn extract-message
  "Extracts the message from x if it is a package, else returns nil"
  [x]
  (when (package? x)
    (:message x)))