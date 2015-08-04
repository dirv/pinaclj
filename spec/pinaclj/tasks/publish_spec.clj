(ns pinaclj.tasks.publish-spec
  (:require [speclj.core :refer :all]
            [pinaclj.tasks.publish :refer :all]
            [pinaclj.nio :as nio]
            [pinaclj.test-fs :as test-fs]))

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

(defn- read-page [fs path]
  (clojure.string/join "\n" (nio/read-all-lines (nio/resolve-path fs "test.md"))))

(describe "publish-path"
  (with fs (test-fs/create-from [simple-page]))
  (before (publish-path @fs "test.md" test-time))
  (it "publishes-path"
    (should-contain "published-at: test-time\n" (read-page @fs "test.md")))
  (it "maintains content"
    (should-contain "\n---\nhello" (read-page @fs "test.md")))
  (it "maintains other headers"
    (should-contain "title: a\n" (read-page @fs "test.md"))))

