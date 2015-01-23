(ns pinaclj.core.cli-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.cli :as cli]
            [pinaclj.core.compile :as cmp]))

(describe "cli"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke cmp/compile-all {} (cli/-main "-h"))))
