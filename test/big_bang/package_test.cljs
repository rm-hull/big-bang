(ns big-bang.package-test
  (:use-macros [cljs-test.macros :only [deftest is= is]])
  (:require [cljs-test.core :as test]
            [big-bang.package :refer [package? make-package extract-message extract-world-state]]))

(deftest make-and-extract-from-packages
  (is (package? ^{:message "MESSAGE"}{:world-state "STATE"}))
  (is (package? ^{:message nil}{:world-state "STATE"}))
  (is (package? ^{:message "MESSAGE"}{:world-state nil}))
  (is (not (package? nil)))
  (is (not (package? "STATE")))
  (is (package? (make-package "STATE" "MESSAGE")))
  (is= ^{:message "MESSAGE"}{:world-state "STATE"} (make-package "STATE" "MESSAGE"))
  (is (nil? (extract-message "STATE")))
  (is (nil? (extract-message ^{:message "MESSAGE"}{})))
  (is= "MESSAGE" (extract-message (make-package "STATE" "MESSAGE")))
  (is= "STATE" (extract-world-state "STATE"))
  (is= "STATE" (extract-world-state ^{:message "MESSAGE"}{:world-state "STATE"}))
  (is= {:message "MESSAGE"} (extract-world-state {:message "MESSAGE"}))
  (is= "STATE" (extract-world-state (make-package "STATE" "MESSAGE"))))
