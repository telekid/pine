(ns pine.re-frame
  (:require [re-frame.core :as re-frame]
            [clojure.data :as data]
            [pine.router :as router]))


;; -- Subscriptions ------------------------------------------------------------

(re-frame/reg-sub
 :pine/location
 (fn [db]
   (:pine/location db)))

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


;; -- Events ------------------------------------------------------------------

(re-frame/reg-event-fx
 :pine/navigate
 (fn [_ [_ route-id params]]
   {:pine/update-url (router/path-for route-id params)
    :dispatch [:pine/handle-navigation]}))

(re-frame/reg-event-fx
 :pine/handle-url-change
 (fn [_ [_ url]]
   {:dispatch [:pine/handle-navigation (router/match-route url)]}))

(re-frame/reg-event-fx
 :pine/handle-navigation
 (fn [cofx [_ next-location]]
   (let [location (:pine/location (:db cofx))
         [leaving entering retained] (data/diff (:active location) (:active next-location))
         effects (apply concat (list (map #(vector :leave %) leaving)
                                     (map #(vector :enter %) entering)
                                     (map #(vector :retain %) retained)))]
     {:db (merge (:db cofx) {:pine/location next-location})
      :dispatch-n effects})))
