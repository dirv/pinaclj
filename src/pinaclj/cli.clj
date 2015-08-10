(ns pinaclj.cli
  (:require [leiningen.generate :as lein])
  (:gen-class))

(defn -main [& args]
  (lein/generate {} args))
