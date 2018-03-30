(ns pine.core
  (:require [clojure.string :as string]))

(declare match-route*
         match-subpath
         pop-result
         push-result
         make-result
         push-children
         reduce-results)

(defn match-route
  [path routes] (match-route* (push-children [] routes) [(make-result path nil nil)]))

(defn- match-route*
  ([routes result-stack]
   (when-let [child-set (first routes)]
     (let [last-result (peek result-stack)
           current-route (first child-set)
           remaining-routes (update-in routes [0] #(into [] (rest %)))]
       (if (empty? child-set)
         (recur (into [] (rest remaining-routes)) (pop-result result-stack))
         (if-let [match-result (match-subpath (:path current-route)
                                              (:remaining-path last-result))]
           (let [new-result (make-result (:remaining-path match-result)
                                         (:params match-result)
                                         (:route current-route))]
             (if (nil? (:remaining-path match-result))
               (reduce-results (rest (conj result-stack new-result)))
               (if (:children current-route)
                 (recur (push-children remaining-routes (:children current-route))
                        (push-result result-stack new-result))
                 (recur remaining-routes result-stack))))
           (recur remaining-routes result-stack)))))))

;; match-route helper functions
(defn- pop-result [result-stack] (pop result-stack))

(defn- push-result [result-stack result] (conj result-stack result))

(defn- make-result [path params route]
  {:remaining-path path
   :params params
   :route route})

(defn- push-children [routes children]
  (into [] (concat [children] routes)))

(defn- reduce-results [results]
  (reduce #(do (cond-> %1
                 (not (nil? (:params %2))) (update-in [:params] merge {(:route %2) (:params %2)})
                 true (update-in [:active] conj (:route %2))))
          {:active #{}} results))


(declare traverse-vector-path)

(defrecord SubpathMatch [remaining-path params])

(defmulti match-subpath
  "Check to see if a `subpath` matches a `test`.

   If the subpath matches the test, break down the test
   params and return an object with a next-subpath and
   the params.

   returns nil if not a match.

   If `test` is a string, match against that string.

   If `test` is a vector, match against strings in the vector,
   and transform :keywords into route parameters.
   Note: Keyword matches will not proceed past '/'.
  "
  ;; TODO Create record for match
  (fn [test subpath] (type test)))

(defmethod match-subpath
  #?(:clj java.lang.String
     :cljs js/String)
  [test subpath]

  (when (string/starts-with? subpath test)
    (SubpathMatch. (let [remainder (string/replace-first subpath test "")]
                     (when (not (empty? remainder))
                       remainder))
                   nil)))

(defmethod match-subpath
  #?(:clj clojure.lang.PersistentVector
     :cljs cljs.core/PersistentVector)
  [test subpath]

  ;; We only want to search up until the first trailing slash,
  ;; not including any trailing slash at the start of the string
  ;; TODO: This regex should just be a conditional
  (let [match (re-find (re-pattern "(\\/?[^\\/\\n]+)(\\/.*)?") subpath)
        focus (second match)
        remainder (match 2)]
    (when-let [params (traverse-vector-path test focus {})]
      (SubpathMatch. remainder params))))

(defn- traverse-vector-path [test path params]
  (if (empty? test)
    params
    (let [current (first test)]
      (cond
        (= (type current) #?(:clj java.lang.String
                             :cljs js/String))
        (let [next-string (string/replace-first path current "")]
          (when (and (string/starts-with? path current) (not (empty? next-string)))
            (recur (rest test)
                  next-string
                  params)))
        (= (type current) #?(:clj clojure.lang.Keyword
                             :cljs cljs.core/Keyword))
        (let [lookahead (or (second test) "")]
          (when (string/includes? path lookahead)
            (let [regex-result (re-find (re-pattern (str "(.*)(" lookahead ".*)")) path)
                  match (second regex-result)
                  remainder (regex-result 2)]
              (recur (rest test)
                     remainder
                     (assoc params current match)))))))))

