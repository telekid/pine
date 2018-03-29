(ns pine.core)

(defn match-route [path routes] false)

(defn add-children [routes]
  (reduce (fn [result [route body]]
            (let [parent (:parent body)]
              (loop [p parent
                     r result]
                (if (not (nil? p))
                  (recur (:parent (p result))
                         (update-in r [p :children] #(conj % route)))
                  r))))
          routes
          routes))
