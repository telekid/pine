(ns pine.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [pine.core :refer [match-route
                               add-children]]))



(deftest test-add-children
  (def pre-add-children
    {:a {:duck true}
     :b {:parent :a
         :bear true}
     :c {:parent :b
         :goat true}})

  (def post-add-children
    {:a {:duck true
         :children [:c :b]}
     :b {:parent :a
         :bear true
         :children [:c]}
     :c {:parent :b
         :goat true}})

  (is (= (add-children pre-add-children) post-add-children)))
