(defproject flow "0.1.0-SNAPSHOT"
  :description "Web content system"
  :url "https://github.com/dirv/flow"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.0"]
                 [ring/ring-defaults "0.1.2"]]
  :plugins [[lein-ring "0.8.13"]
            [speclj "3.1.0"]]
  :ring {:handler flow.core.handler/app}
  :test-paths ["spec"]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [speclj "3.1.0"]]}})
