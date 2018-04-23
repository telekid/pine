(ns pine.router
  (:require [pine.core :as core]))

(def routes (atom []))

(defn set-routes!
  "Set your application's routes."
  [new-routes]
  (reset! routes new-routes))

(defn match-route
  "Single argument version of `pine.core/match-route`."
  [path] (core/match-route path @routes))

(defn path-for
  "Two argument version of `pine.core/path-for`."
  [route-id params] (core/path-for route-id params @routes))
