(ns pine.re-frame
  (:require [re-frame.core :as re-frame]
            [pine.router :as router]))

(re-frame/reg-sub
 :pine/location
 (fn [db]
   (:location db)))

(re-frame/reg-sub
 :pine/active-routes
 :<- [:pine/location]
 (fn [location _]
   (:active location)))

(re-frame/reg-sub
 :pine/route-params
 :<- [:pine/location]
 (fn [location _]
   (:params location)))
