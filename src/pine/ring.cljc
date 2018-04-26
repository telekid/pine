(ns pine.ring
  (:require [pine.router :refer [match-route]]))

;; TODO This probably shouldn't rely on router match-route
(defn make-handler [handler-lookup]
  (fn [{:keys [uri] :as req}]
    (let [{:keys [route params]} (match-route uri)
          handler (handler-lookup route)]
      (handler
       (-> req
           (update-in [:params] merge params)
           (update-in [:route-params] merge params))))))
