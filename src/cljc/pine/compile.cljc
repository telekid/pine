(ns pine.compile)

;; TODO: Refactor this function, which is copied from core.cljc
(defn- push-children [routes children]
  (into [] (concat [children] routes)))

(defn compile-routes* [routes-stack result-stack result]
  (when-let [child-set (first routes-stack)]
    (if (empty? child-set)
      (if (not (empty? (rest routes-stack)))
        (recur (into [] (rest routes-stack)) (pop result-stack) result)
        result)
      (let [current-route (first child-set)
            child-routes (:routes current-route)
            remaining-routes (update-in routes-stack [0] #(into [] (rest %)))
            next-result-stack (conj result-stack (dissoc current-route :routes))
            next-result (assoc result (:route-id current-route) next-result-stack)]
        (if child-routes
          (recur (push-children remaining-routes child-routes) next-result-stack next-result)
          (recur remaining-routes (pop next-result-stack) next-result))))))

(defn compile-routes [routes]
  (compile-routes* (push-children [] routes) [] {}))
