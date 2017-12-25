(defproject pinaclj "0.1.0-SNAPSHOT"
  :description "Simple web content system"
  :url "https://github.com/dirv/pinaclj"
  :min-lein-version "2.5.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/java.classpath "0.2.3"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/tools.cli "0.3.5"]
                 [com.google.jimfs/jimfs "1.1"]
                 [com.taoensso/tower "3.0.2"]
                 [endophile "0.2.1"]
                 [enlive "1.1.6"]]
  :eval-in-leiningen true
  :aliases {"test" ["spec"]}
  :plugins [[speclj "3.3.1"] [lein-deps-tree "0.1.2"]]
  :test-paths ["spec"]
  :profiles {:dev {:dependencies [[speclj "3.3.1"]
                                  [leiningen-core "2.5.3"]]}})
