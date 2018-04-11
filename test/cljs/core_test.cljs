(ns pine.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [pine.core :as core]))

;; (deftest make-sorted
;;   (def pre-sorted
;;     {:a {:duck true}
;;      :b {:parent :a
;;          :bear true}
;;      :c {:parent :b
;;          :goat true}})

;;   (def post-sorted
;;     (into (sorted-map) {:a {:duck true}
;;                         :b {:parent :a
;;                             :bear true}
;;                         :c {:parent :b
;;                             :goat true}})))

  ;; (is (= (core/make-sorted pre-sorted) post-sorted)))


;; (deftest test-add-children
;;   (def pre-add-children (core/make-sorted {:a {:duck true}
;;                                            :b {:parent :a
;;                                                :bear true}
;;                                            :c {:parent :b
;;                                                :goat true}
;;                                            :d {:parent :a
;;                                                :deer true}}))

;;   (def post-add-children (core/make-sorted {:a {:duck true
;;                                                 :descendents [[:b :c] [:d]]}
;;                                             :b {:parent :a
;;                                                 :bear true
;;                                                 :descendents [[:b]]}
;;                                             :c {:parent :b
;;                                                 :goat true}})))

  ;; (is (= (core/add-children pre-add-children) post-add-children)))


(def post-nest-routes
  [{:route-id :port
    :test-path "/port"}
   {:route-id :portfolioabc
    :test-path "/portfolioabc"}
   {:route-id :portfolio
    :test-path "/portfolio"
    :routes [{:route-id :view
              :test-path ["/view-" :id]
              :routes [{:route-id :page
                        :test-path "/page"}]}
             {:route-id :about-portfolio
              :test-path "/about"}]}
   {:route-id :home
    :test-path "/home"}
   {:route-id :portfolioo
    :test-path "/portfolioo"}
   {:route-id :port2
    :test-path "/port"}])

(deftest match-route
  (is (= (core/match-route "/port" post-nest-routes)
         {:active #{:port}}))

  (is (= (core/match-route "/portfolioabc" post-nest-routes)
         {:active #{:portfolioabc}}))

  (is (= (core/match-route "/portfolio" post-nest-routes)
         {:active #{:portfolio}}))

  (is (= (core/match-route "/portfolio/view" post-nest-routes)
         nil))

  (is (= (core/match-route "/portfolio/view-" post-nest-routes)
         nil))

  (is (= (core/match-route "/portfolio/view-123" post-nest-routes)
         {:params {:view {:id "123"}} :active #{:portfolio :view}}))

  (is (= (core/match-route "/portfolio/view-123/page" post-nest-routes)
         {:params {:view {:id "123"}} :active #{:portfolio :view :page}}))

  (is (= (core/match-route "/portfolio/about" post-nest-routes)
         {:active #{:portfolio :about-portfolio}}))

  (is (= (core/match-route "/home" post-nest-routes)
         {:active #{:home}}))

  (is (= (core/match-route "/portfolioo" post-nest-routes)
         {:active #{:portfolioo}})))
