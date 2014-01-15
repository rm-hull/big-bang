(ns big-bang.core-test
  (:use-macros [cljs-test.macros :only [deftest is= is]])
  (:require [cljs-test.core :as test]
            [big-bang.core :refer [package? make-package extract-message extract-world-state]]))

(deftest make-and-extract-from-packages
  (is (package? {:world-state "STATE" :message "MESSAGE"}))
  (is (package? {:world-state "STATE" :message nil}))
  (is (package? {:world-state nil :message "MESSAGE"}))
  (is (not (package? nil)))
  (is (not (package? "STATE")))
  (is (package? (make-package "STATE" "MESSAGE")))
  (is= {:world-state "STATE" :message "MESSAGE"} (make-package "STATE" "MESSAGE"))
  (is= nil (extract-message "STATE"))
  (is= "MESSAGE" (extract-message {:message "MESSAGE"}))
  (is= "MESSAGE" (extract-message (make-package "STATE" "MESSAGE")))
  (is= "STATE" (extract-world-state "STATE"))
  (is= "STATE" (extract-world-state {:world-state "STATE" :message "MESSAGE"}))
  (is= {:message "MESSAGE"} (extract-world-state {:message "MESSAGE"}))
  (is= "STATE" (extract-world-state (make-package "STATE" "MESSAGE"))))