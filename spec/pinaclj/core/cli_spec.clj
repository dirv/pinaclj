(ns pinaclj.core.cli-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.cli :as cli]
            [pinaclj.core.pages.compile :as cmp]))

(describe "cli"
  (around [it] (binding [*out* (new java.io.StringWriter)] (it)))

  (it "does not run compile when requesting help"
    (should-not-invoke cmp/compile-all {} (cli/-main "-h")))

  ; Not sure these need to be tested? Better to just test cli-options.
  (xit "runs compile with defaults when no overrides specified"
    (should-invoke cmp/compile-all {:with ["drafts" "published" :*]} (cli/-main)))

  (xit "runs compile with overrides when short switches specified"
    (should-invoke cmp/compile-all {:with ["source" "destination" :*]} (cli/-main "-s" "source" "-d" "destination")))

  (xit "runs compile with overrides when long switches specified"
    (should-invoke cmp/compile-all {:with ["source" "destination" :*]} (cli/-main "--source" "source" "--destination" "destination"))))
