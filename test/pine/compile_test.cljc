(ns pine.compile-test
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing run-tests]]
               :clj [clojure.test :refer :all])
            [pine.compile :as compile]))

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

(def compile-result
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

(deftest compile-routes
  (is (= (compile/compile-routes post-nest-routes)
         compile-result)))
