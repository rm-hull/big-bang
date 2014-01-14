(ns big-bang.core-test
  (:use-macros [cljs-test.macros :only [deftest is= is]])
  (:require [cljs-test.core :as test]
            [big-bang.core :refer [package? make-package]]))

(deftest package
  (is (package? {:world-state "STATE" :message "MESSAGE"}))
  (is (package? {:world-state "STATE" :message nil}))
  (is (package? {:world-state nil :message "MESSAGE"}))
  (is (not (package? nil)))
  (is (not (package? "STATE")))
  (is (package? (make-package "STATE" "MESSAGE")))
  (is= {:world-state "STATE" :message "MESSAGE"} (make-package "STATE" "MESSAGE")))