(defproject pine "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj" "src/cljc"]
  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.15"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   :plugins [[lein-figwheel "0.5.15"]
                             [lein-npm "0.6.2"]
                             [lein-doo "0.1.10"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :source-paths ["src/clj" "src/cljc"]}}
  :aliases {"test" ["doo" "chrome"]}
  :figwheel {}
  :npm {:devDependencies [[karma "2.0.0"]
                          [karma-cljs-test "0.1.0"]
                          [karma-chrome-launcher "2.2.0"]]}
  :doo {:build "test"}
  :cljsbuild
  {:builds [{:id "main"
             :figwheel true
             :source-paths ["src/cljs" "src/cljc" "dev"]
             :compiler {:asset-path "js/out"
                        :main pine.core
                        :output-to "resources/public/js/main.js"
                        :output-dir "resources/public/js/out"
                        :optimizations :none}}
            {:id "test"
             :source-paths ["src/cljc" "test/cljs"]
             :compiler {:main pine.runner
                        :output-to "resources/public/js/testable.js"}}]})



