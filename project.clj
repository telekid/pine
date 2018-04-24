(defproject telekid/pine :lein-v
  :description "A Clojure/ClojureScript router designed for universal applications."
  :url "https://github.com/telekid/pine"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]]
  :plugins [[com.roomkey/lein-v "6.3.0"]]
  :source-paths ["src"]

  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"] ;; compute new version & tag it
                  ["vcs" "push"]]

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.15"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [doo "0.1.10"]
                                  [org.clojure/test.check "0.10.0-alpha2"]]
                   :plugins [[lein-figwheel "0.5.15"]
                             [lein-npm "0.6.2"]
                             [lein-cljsbuild "1.1.7"]
                             [lein-doo "0.1.10"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :aliases {"test-cljs" ["doo" "chrome"]}
  :prep-tasks [["v" "cache" "src"]]
  :figwheel {}
  :npm {:devDependencies [[karma "2.0.0"]
                          [karma-cljs-test "0.1.0"]
                          [karma-chrome-launcher "2.2.0"]]}
  :doo {:build "test-cljs"}
  :cljsbuild
  {:builds [{:id "main"
             :figwheel true
             :source-paths ["src"]
             :compiler {:asset-path "js/out"
                        :main pine.core
                        :output-to "resources/public/js/main.js"
                        :output-dir "resources/public/js/out"
                        :optimizations :none}}
            {:id "test-cljs"
             :source-paths ["src" "test"]
             :compiler {:main "pine.runner"
                        :output-to "resources/public/js/testable.js"}}]})



