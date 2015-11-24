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

(def page-2
  {:path "test2.md"
   :content "title: a\n---\nhello"})

(def rewrite
  {:path "rewrite.md"
   :content "title: test\npublished-at: 2015-01-01T15:00:00.102Z\n---\ntest2"})

(def no-separator
  {:path "no-separator.md"
   :content "title: test\ntest2"})

(defn- publish-one [fs path]
  (last (publish fs [path] test-time)))

(describe "publish"
  (with fs (create-from [simple-page rewrite page-2 no-separator]))

  (describe "with unpublished page"
    (it "outputs published message"
      (let [message (publish-one @fs "test.md")]
        (should-contain "has been published at" (:msg message))
        (should-contain "test.md" (:msg message))
        (should= :success (:type message))))
    (describe "resulting page"
      (before (publish-one @fs "test.md"))
      (it "publishes-path"
        (should-contain "published-at: test-time\n" (read-file @fs "test.md")))
      (it "maintains content"
        (should-contain "\n---\nhello" (read-file @fs "test.md")))
      (it "maintains other headers"
        (should-contain "title: a\n" (read-file @fs "test.md")))))

  (describe "with published path"
    (it "outputs error message"
      (let [message (publish-one @fs "rewrite.md")]
        (should-contain "already published" (:msg message))
        (should-contain "rewrite.md" (:msg message))
        (should= :error (:type message))))
    (it "does not touch file"
      (publish-one @fs "test.md")
      (should= (:content rewrite) (read-file @fs "rewrite.md"))))

  (describe "with unknown file"
    (it "outputs error message"
      (let [message (publish-one @fs "unknown.md")]
        (should-contain "not found" (:msg message))
        (should-contain "unknown.md" (:msg message))
        (should= :error (:type message))))
    (it "doesn't write file"
      (publish-one @fs "unknown.md")
      (should-not (file-exists? @fs "unknown.md"))))

  (describe "with multiple files"
    (it "outputs two messages"
      (should= 2 (count (publish @fs ["test.md" "test2.md"] test-time))))
    (it "publishes two files"
      (publish @fs ["test.md" "test2.md"] test-time)
      (should-contain "published-at" (read-file @fs "test.md"))
      (should-contain "published-at" (read-file @fs "test2.md"))))
          
  (describe "with invalid source file"
    (it "outputs fatal message when separator missing"
        (let [message (publish-one @fs "no-separator.md")]
          (should-contain "no header separator of '---'" (:msg message))
          (should-contain "no-separator.md" (:msg message))
          (should= :fatal (:type message))))))
