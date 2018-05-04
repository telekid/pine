(ns pine.ring
  (:require [pine.router :refer [match-route]]))

;; TODO This probably shouldn't rely on router match-route
(defn make-handler
  "Generate a ring handler for Pine.
   `handler-lookup` is a function that takes a route keyword
    and returns a handler."
  [handler-lookup]

  (fn [{:keys [uri] :as req}]
    (when-let [{:keys [route params]} (match-route uri)]
      (let [handler (handler-lookup route)]
        (-> req
            (update-in [:params] merge params)
            (update-in [:route-params] merge params)
            (handler))))))
