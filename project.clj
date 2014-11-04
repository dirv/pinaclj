(defproject flow "0.1.0-SNAPSHOT"
  :description "Web content system"
  :url "https://github.com/dirv/flow"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.google.jimfs/jimfs "1.0"]
                 [enlive "1.1.5"]
                 [ring/ring-defaults "0.1.2"]]
  :plugins [[lein-ring "0.8.13"]
            [speclj "3.1.0"]]
  :ring {:handler flow.core.handler/app}
  :test-paths ["spec"]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [clj-time "0.8.0"]
                        [peridot "0.3.0"]
                        [speclj "3.1.0"]]}})
