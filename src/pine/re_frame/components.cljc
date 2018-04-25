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

  `route-id` is the route name for this component.

  Following `route-id`, you may include a list of keywords
  indicating route segments that should suppress rendering.

  Finally, you may include one or more components to be
  rendered into the component.

  e.g.
  [view :home [home]]
  [view :home :home-welcome [home]]
  [view :home :home-welcome [home] [goat]]
  "

  [route-id & vargs]
  (let [suppress (into #{} (take-while keyword? vargs))
        children (drop-while keyword? vargs)
        active-routes @(re-frame/subscribe [:pine/active-routes])]
    (when (and (contains? active-routes route-id)
               (empty? (clojure.set/intersection suppress active-routes)))
      (into [:<>] children))))
