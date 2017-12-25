(ns pinaclj.cli-spec
  (:require [speclj.core :refer :all]
            [pinaclj.tasks.generate :as task]
            [pinaclj.cli :as cli]))

(describe "cli"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke task/generate {} (cli/run "-h"))))

