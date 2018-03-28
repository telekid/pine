(ns pine.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest test-identity
  (is (= 1 1)))

