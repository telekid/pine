(ns pine.core-test
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing run-tests]]
               :clj [clojure.test :refer :all])
            [pine.core :as core]))

(def routes
  [{:route-id :port
    :pattern "/port"}
   {:route-id :portfolioabc
    :pattern "/portfolioabc"}
   {:route-id :portfolio
    :pattern "/portfolio"
    :routes [{:route-id :view
              :pattern ["/view-" :id]
              :routes [{:route-id :page
                        :pattern "/page"}]}
             {:route-id :about-portfolio
              :pattern "/about"}]}
   {:route-id :home
    :pattern "/home"}
   {:route-id :portfolioo
    :pattern "/portfolioo"}
   {:route-id :port2
    :pattern "/port"}
   {:route-id :public
    :pattern "/public"
    :routes [{:route-id :public-item
              :pattern true}]}])

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
  {:port [{:route-id :port :pattern "/port"}]
   :portfolioabc [{:route-id :portfolioabc :pattern "/portfolioabc"}]
   :portfolio [{:route-id :portfolio :pattern "/portfolio"}]
   :view [{:route-id :portfolio :pattern "/portfolio"}
          {:route-id :view :pattern ["/view-" :id]}]
   :page [{:route-id :portfolio :pattern "/portfolio"}
          {:route-id :view :pattern ["/view-" :id]}
          {:route-id :page :pattern "/page"}]
   :about-portfolio [{:route-id :portfolio :pattern "/portfolio"}
                     {:route-id :about-portfolio :pattern "/about"}]
   :home [{:route-id :home :pattern "/home"}]
   :portfolioo [{:route-id :portfolioo :pattern "/portfolioo"}]
   :port2 [{:route-id :port2 :pattern "/port"}]
   :public [{:route-id :public :pattern "/public"}]
   :public-item [{:route-id :public :pattern "/public"}
                 {:route-id :public-item :pattern true}]})

(deftest routes-by-key
  (is (= (core/routes-by-key routes)
         keyed-routes)))
