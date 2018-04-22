(ns pine.core-test
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing run-tests]]
               :clj [clojure.test :refer :all])
            [pine.core :as core]))

(def routes
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
  (is (= (core/match-route "/port" routes)
         {:active #{:port}}))

  (is (= (core/match-route "/portfolioabc" routes)
         {:active #{:portfolioabc}}))

  (is (= (core/match-route "/portfolio" routes)
         {:active #{:portfolio}}))

  (is (= (core/match-route "/portfolio/view" routes)
         nil))

  (is (= (core/match-route "/portfolio/view-" routes)
         nil))

  (is (= (core/match-route "/portfolio/view-123" routes)
         {:params {:view {:id "123"}} :active #{:portfolio :view}}))

  (is (= (core/match-route "/portfolio/view-123/page" routes)
         {:params {:view {:id "123"}} :active #{:portfolio :view :page}}))

  (is (= (core/match-route "/portfolio/about" routes)
         {:active #{:portfolio :about-portfolio}}))

  (is (= (core/match-route "/home" routes)
         {:active #{:home}}))

  (is (= (core/match-route "/portfolioo" routes)
         {:active #{:portfolioo}})))

(def keyed-routes
  {:port [{:route-id :port :test-path "/port"}]
   :portfolioabc [{:route-id :portfolioabc :test-path "/portfolioabc"}]
   :portfolio [{:route-id :portfolio :test-path "/portfolio"}]
   :view [{:route-id :portfolio :test-path "/portfolio"}
          {:route-id :view :test-path ["/view-" :id]}]
   :page [{:route-id :portfolio :test-path "/portfolio"}
          {:route-id :view :test-path ["/view-" :id]}
          {:route-id :page :test-path "/page"}]
   :about-portfolio [{:route-id :portfolio :test-path "/portfolio"}
                     {:route-id :about-portfolio :test-path "/about"}]
   :home [{:route-id :home :test-path "/home"}]
   :portfolioo [{:route-id :portfolioo :test-path "/portfolioo"}]
   :port2 [{:route-id :port2 :test-path "/port"}]})

(deftest routes-by-key
  (is (= (core/routes-by-key routes)
         keyed-routes)))
