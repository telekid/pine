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
    :test-path "/port"}
   {:route-id :public
    :test-path "/public"
    :routes [{:route-id :public-item
              :test-path true}]}])

(deftest match-route
  (is (= (core/match-route "/port" routes)
         {:active #{:port} :route :port}))

  (is (= (core/match-route "/portfolioabc" routes)
         {:active #{:portfolioabc} :route :portfolioabc}))

  (is (= (core/match-route "/portfolio" routes)
         {:active #{:portfolio} :route :portfolio}))

  (is (= (core/match-route "/portfolio/view" routes)
         nil))

  (is (= (core/match-route "/portfolio/view-" routes)
         nil))

  (is (= (core/match-route "/portfolio/view-123" routes)
         {:params {:view {:id "123"}} :active #{:portfolio :view} :route :view}))

  (is (= (core/match-route "/portfolio/view-123/page" routes)
         {:params {:view {:id "123"}} :active #{:portfolio :view :page} :route :page}))

  (is (= (core/match-route "/portfolio/about" routes)
         {:active #{:portfolio :about-portfolio} :route :about-portfolio}))

  (is (= (core/match-route "/home" routes)
         {:active #{:home} :route :home}))

  (is (= (core/match-route "/portfolioo" routes)
         {:active #{:portfolioo} :route :portfolioo}))

  (is (= (core/match-route "/public" routes)
         {:active #{:public} :route :public}))

  (is (= (core/match-route "/public/style.css" routes)
         {:active #{:public :public-item} :route :public-item})))

(deftest path-for
  (is (= (core/path-for :portfolio {} routes) "/portfolio"))

  (is (= (core/path-for :about-portfolio {} routes) "/portfolio/about"))

  (is (= (core/path-for :view {:view {:id "home"}} routes) "/portfolio/view-home"))

  (is (= (core/path-for :view {:view {:id 123}} routes) "/portfolio/view-123"))

  (is (= (core/path-for :view {:view {:id :untapt}} routes) "/portfolio/view-untapt"))

  (is (= (core/path-for :home {} routes) "/home")))


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
   :port2 [{:route-id :port2 :test-path "/port"}]
   :public [{:route-id :public :test-path "/public"}]
   :public-item [{:route-id :public :test-path "/public"}
                 {:route-id :public-item :test-path true}]})

(deftest routes-by-key
  (is (= (core/routes-by-key routes)
         keyed-routes)))
