(ns pinaclj.cli-spec
  (:require [speclj.core :refer :all]
            [pinaclj.cli :as cli]
            [pinaclj.compile :as cmp]))

(describe "cli"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke cmp/compile-all {} (cli/-main "-h"))))
