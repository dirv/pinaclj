(ns leiningen.generate-spec
  (:require [speclj.core :refer :all]
            [leiningen.generate :as plugin]
            [pinaclj.tasks.generate :as task]))

(describe "leiningen plugin"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke task/generate {} (plugin/generate {} "-h"))))
