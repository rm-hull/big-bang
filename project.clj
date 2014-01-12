(defproject rm-hull/inkspot "0.0.1-SNAPSHOT"
  :description "big-bang: a ClojureScript game loop, loosely based on Racket's big-bang and implemented on top of core.async"
  :url "https://github.com/rm-hull/big-bang"
  :clojurecript? true
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2134"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [rm-hull/cljs-dataview "0.0.1-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.0.1"]]
  :source-paths ["src"]
  :cljsbuild {
    :builds {
      :main {
        :source-paths ["src"]
        :jar true
        :incremental? true
        :compiler {
          :output-to "target/big-bang.js"
          :source-map "target/big-bang.map"
          :static-fns true
          ;:optimizations :advanced
          :pretty-print true }}
      :examples {
        :source-paths ["src" "examples"]
        :incremental? true
        :compiler {
          :output-to "target/example.js"
          :source-map "target/example.map"
          :static-fns true
          ;:optimizations :advanced
          :pretty-print true }}}}
  :min-lein-version "2.3.4"
  :global-vars {*warn-on-reflection* true})
