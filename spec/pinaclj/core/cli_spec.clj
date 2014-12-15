(ns pinaclj.core.cli-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.cli :as cli]
            [pinaclj.core.pages.compile :as cmp]))

(describe "cli"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke cmp/run {} (cli/-main "-h")))

  (it "runs compile with defaults when no overrides specified"
    (should-invoke cmp/run {:with ["drafts" "published"]} (cli/-main)))

  (it "runs compile with overrides when short switches specified"
    (should-invoke cmp/run {:with ["source" "destination"]} (cli/-main "-s" "source" "-d" "destination")))

  (it "runs compile with overrides when long switches specified"
    (should-invoke cmp/run {:with ["source" "destination"]} (cli/-main "--source" "source" "--destination" "destination"))))
