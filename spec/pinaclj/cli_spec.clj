(ns pinaclj.cli-spec
  (:require [speclj.core :refer :all]
            [pinaclj.cli :as cli]
            [pinaclj.core :as core]))

(describe "cli"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke core/compile-all {} (cli/-main "-h"))))
