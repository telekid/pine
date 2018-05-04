(ns pine.ring-test
  (:require [pine.ring :as sut]
            [pine.router :as router]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])))

(def routes
  [{:route-id :home
    :pattern "/home"}])

(router/set-routes! routes)

(defn home-handler [req]
  :home-handler)

(def handlers {:home home-handler})

(defn handler-fn [route] (route handlers))

(t/deftest make-handler
  (let [handler (sut/make-handler handler-fn)]
    (t/is (= (handler {:uri "/home"}) :home-handler))
    (t/is (= (handler {:uri "/meow"}) nil))))
