(ns pine.core
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]))

(s/def ::full-path string?)

(s/def ::routes (s/coll-of ::route))

(s/def ::route (s/keys :req-un [::route-id ::pattern]
                       :opt [::children]))

(s/def ::route-id keyword?)

(s/def ::string-pattern string?)

(s/def ::seq-pattern vector?)

(s/def ::params map?)

(s/def ::pattern (s/or :seq-pattern ::seq-pattern
                       :string-pattern ::string-pattern))

(s/def ::match-result (s/keys :req-un [::active]
                              :opt-un [::params]))

(s/def ::active (s/coll-of keyword? :kind set? :min-count 1))

(declare match-route*
         match-subpath
         pop-result
         push-result
         make-result
         push-children
         reduce-results
         routes-by-key)

(s/fdef match-route
        :args (s/cat :path ::full-path :routes ::routes)
        :ret ::match-result)

(defn match-route
  "Match a `path` against a `routes` definition."
  [path routes]

  (match-route* (push-children [] routes) [(make-result path nil nil)]))

(defn- match-route*
  ([routes result-stack]
   (when-let [child-set (first routes)]
     (let [last-result (peek result-stack)
           current-route (first child-set)
           remaining-routes (update-in routes [0] #(into [] (rest %)))]
       (if (empty? child-set)
         (recur (into [] (rest remaining-routes)) (pop-result result-stack))
         (if-let [match-result (match-subpath (:pattern current-route)
                                              (:remaining-path last-result))]
           (let [new-result (make-result (:remaining-path match-result)
                                         (:params match-result)
                                         (:route-id current-route))]
             (if (nil? (:remaining-path match-result))
               (assoc (reduce-results (rest (conj result-stack new-result)))
                      :route (:route-id current-route))
               (if (:routes current-route)
                 (recur (push-children remaining-routes (:routes current-route))
                        (push-result result-stack new-result))
                 (recur remaining-routes result-stack))))
           (recur remaining-routes result-stack)))))))

;; match-route helper functions
(defn- pop-result [result-stack] (pop result-stack))

(defn- push-result [result-stack result] (conj result-stack result))

(defn- make-result [path params route-id]
  {:remaining-path path
   :params params
   :route-id route-id})

(defn- push-children [routes children]
  (into [] (concat [children] routes)))

(defn- reduce-results [results]
  (reduce #(do (cond-> %1
                 (not (nil? (:params %2))) (update-in [:params] merge {(:route-id %2) (:params %2)})
                 true (update-in [:active] conj (:route-id %2))))
          {:active #{}} results))

(declare traverse-seq-path)

(defprotocol PathParam
  "A parameter of a path item."

  (to-string [param]
    "Convert a path param to a string for path construction.

     Example:
     (to-string :home)
     \"home\"

     (to-string 123)
     \"123\"

     (to-string \"abc\")
     \"abc\""))

(extend-protocol PathParam
  #?(:clj java.lang.String
     :cljs string)
  (to-string [param] param)

  #?(:clj clojure.lang.Keyword
     :cljs cljs.core/Keyword)
  (to-string [param] (name param))

  #?(:clj java.lang.Long
     :cljs number)
  (to-string [param] (str param)))

(defprotocol SeqPatternItem
  "An item within a sequential pattern."

  (segment-item [item subparams]
    "Construct a path segment item with given params.

     Throws if required param is missing."))

(extend-protocol SeqPatternItem
  #?(:clj java.lang.String
     :cljs string)
  (segment-item [item subparams] item)

  #?(:clj clojure.lang.Keyword
     :cljs cljs.core/Keyword)
  (segment-item [item subparams]
    (if-let [param (item subparams)]
      (to-string param)
      (throw (ex-info "Missing parameter required to make path segment."
                      {:missing-param item})))))

(defrecord SubpathMatch [remaining-path params])

(defprotocol Pattern
  "A pattern for a route segment."

  (match-subpath [pattern subpath]
    "Check to see if a `subpath` matches a `pattern`.

     If the subpath matches the pattern, break down the pattern
     params and return an object with a next-subpath and
     the params.

     returns nil if not a match.

     If `pattern` is a string, match against that string.

     If `pattern` is a vector, match against strings in the vector,
     and transform :keywords into route parameters.
     Note: Keyword matches will not proceed past '/'.")

  (build-subpath [pattern params]
    "Constructs a route segment from a pattern and params."))

(extend-protocol Pattern
  #?(:clj java.lang.String
     :cljs string)
  (match-subpath [pattern subpath]
    (when (string/starts-with? subpath pattern)
      (SubpathMatch. (let [remainder (string/replace-first subpath pattern "")]
                       (when (not (empty? remainder))
                         remainder))
                     nil)))
  (build-subpath [pattern params] pattern)

  #?(:clj clojure.lang.PersistentVector
     :cljs cljs.core/PersistentVector)
  (match-subpath [pattern subpath]
    ;; We only want to search up until the first trailing slash,
    ;; not including any trailing slash at the start of the string
    ;; TODO: This regex should just be a conditional
    (let [match (re-find (re-pattern "(\\/?[^\\/\\n]+)(\\/.*)?") subpath)
          focus (second match)
          remainder (match 2)]
      (when-let [params (traverse-seq-path pattern focus {})]
        (SubpathMatch. remainder params))))
  (build-subpath [pattern params]
    (string/join (map #(segment-item % params) pattern)))

  #?(:clj java.lang.Boolean
     :cljs boolean)
    (match-subpath [pattern _]
      (SubpathMatch. nil nil))

    (build-subpath [_ _] ""))

(defn- traverse-seq-path [pattern path params]
  (if (empty? pattern)
    params
    (let [current (first pattern)]
      (cond
        (= (type current) #?(:clj java.lang.String
                             :cljs js/String))
        (let [next-string (string/replace-first path current "")]
          (when (and (string/starts-with? path current) (not (empty? next-string)))
            (recur (rest pattern)
                  next-string
                  params)))
        (= (type current) #?(:clj clojure.lang.Keyword
                             :cljs cljs.core/Keyword))
        (let [lookahead (or (second pattern) "")]
          (when (string/includes? path lookahead)
            (let [regex-result (re-find (re-pattern (str "(.*)(" lookahead ".*)")) path)
                  match (second regex-result)
                  remainder (regex-result 2)]
              (recur (rest pattern)
                     remainder
                     (assoc params current match)))))))))

(defn path-for
  "Create a path from a `route-id`, some `params` and a `routes` definition."
  [route-id params routes]

  (let [compiled (routes-by-key routes)]
    (string/join (map #(build-subpath (:pattern %) ((:route-id %) params)) (route-id compiled)))))

(defn- routes-by-key* [routes-stack result-stack result]
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

(defn routes-by-key
  "Convert a `routes` definition to a map of all routes keyed by :route-id.

   Primarily used by path-for. May have other uses."
  [routes]

  (routes-by-key* (push-children [] routes) [] {}))
