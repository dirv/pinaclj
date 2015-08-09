(ns pinaclj.tasks.publish-spec
  (:require [speclj.core :refer :all]
            [pinaclj.tasks.publish :refer :all]
            [pinaclj.test-fs :refer :all]
            [pinaclj.nio :as nio]
            [pinaclj.date-time :as dt]))

(defn- test-time []
  "test-time")

(def simple-page
  {:path "test.md"
   :content "title: a\n---\nhello"})

(def rewrite
  {:path "rewrite.md"
   :content "title: test\npublished-at: 2015-01-01T15:00:00.102Z\n---\ntest2"})

(defn- read-file [fs path]
  (clojure.string/join "\n" (nio/read-all-lines (nio/resolve-path fs path))))

(describe "publish-path"
  (with-all fs (create-from [simple-page rewrite]))
  (describe "with unpublished page"
    (with-all messages (publish-path @fs "test.md" test-time))
    (it "outputs published message"
      (should-contain "was published at" (last @messages))
      (should-contain "test.md" (last @messages)))
    (it "publishes-path"
      (should-contain "published-at: test-time\n" (read-file @fs "test.md")))
    (it "maintains content"
      (should-contain "\n---\nhello" (read-file @fs "test.md")))
    (it "maintains other headers"
      (should-contain "title: a\n" (read-file @fs "test.md"))))
  (describe "with published path"
    (with-all messages (publish-path @fs "rewrite.md" test-time))
    (it "outputs error message"
      (should-contain "already published" (last @messages))
      (should-contain "rewrite.md" (last @messages)))
    (it "does not touch file"
      (should= (:content rewrite) (read-file @fs "rewrite.md"))))
  (describe "with unknown file"
    (with-all messages (publish-path @fs "unknown.md" test-time))
    (it "outputs error message"
      (should-contain "not found" (last @messages))
      (should-contain "unknown.md" (last @messages)))
    (it "doesn't write file"
      (should-not (file-exists? @fs "unknown.md")))))
