(ns pine.re-frame.components
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [pine.router :as router]))

(defn link
  "Create a router-aware link."
  [{:keys [route-id params active-class]
    :as keys
    :or {params {}}}
   & children]
  (let [active-routes (re-frame/subscribe [:pine/active-routes])]
    (into [:a (-> keys
                  (dissoc :route-id :active-class :params)
                  (assoc :href (router/path-for route-id params))
                  ((fn [ks]
                     (if (and (contains? @active-routes route-id)
                              active-class)
                       (update-in ks [:class-name] #(str % " " active-class))
                       ks))))]
          children)))

(defn view
  "Insert a component into the component tree.

  `route` is the route name for this component.
  `component` is either a keyword identifier for the component,
  or the component itself.

  e.g.
  [view :home [home]]
  "
  [route-id & children]
  (when (contains? @(re-frame/subscribe [:pine/active-routes])
                   route-id)
    (into [:<>] children)))

