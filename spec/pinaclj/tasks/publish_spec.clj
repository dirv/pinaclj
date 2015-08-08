(ns pinaclj.tasks.publish-spec
  (:require [speclj.core :refer :all]
            [pinaclj.tasks.publish :refer :all]
            [pinaclj.test-fs :refer :all]
            [pinaclj.nio :as nio]
            [pinaclj.date-time :as dt]))

(defn- test-time []
  "test-time")

(def empty-page {})
(def published-page {:published-at "existing-time"})

(describe "add-header"
  (it "inserts published-at key/value"
    (should= {:published-at "test-time"}
      (add-header empty-page test-time)))

  (it "doesn't insert published-at when already published"
    (should= {:published-at "existing-time"}
      (add-header published-page test-time))))

(def simple-page
  {:path "test.md"
   :content "title: a\n---\nhello"})

(def rewrite
  {:path "rewrite.md"
   :content "title: test\npublished-at: 2015-01-01T15:00:00.102Z\n---\ntest2"})

(defn- read-file [fs path]
  (clojure.string/join "\n" (nio/read-all-lines (nio/resolve-path fs path))))

(describe "publish-path"
  (with fs (create-from [simple-page rewrite]))
  (describe "with unpublished page"
    (before (publish-path @fs "test.md" test-time))
    (it "publishes-path"
      (should-contain "published-at: test-time\n" (read-file @fs "test.md")))
    (it "maintains content"
      (should-contain "\n---\nhello" (read-file @fs "test.md")))
    (it "maintains other headers"
      (should-contain "title: a\n" (read-file @fs "test.md"))))
  (describe "with published path"
    (it "writes out headers in same order as read"
      (publish-path @fs "rewrite.md" dt/now)
      (should= (:content rewrite) (read-file @fs "rewrite.md"))))
  (describe "with unknown file"
    (it "outputs error message"
      (should-contain #"not found" (last (publish-path @fs "unknown.md" dt/now))))
    (it "doesn't write file"
      (publish-path @fs "unknown.md" dt/now)
      (should-not (file-exists? @fs "unknown.md")))))