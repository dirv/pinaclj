(ns pinaclj.core.pages.compile-spec
  (:require [pinaclj.core.pages.compile :refer :all]
            [pinaclj.core.files :as files]
            [pinaclj.core.test-fs :as test-fs]
            [pinaclj.core.templates :as templates]
            [speclj.core :refer :all]))

(def test-page
  [{:path "drafts/post.md"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n\n###Markdown header\nMarkdown paragraph."}])

(defn compiled []
  (run "drafts" "published" templates/page)
  (files/content "published/post.html"))

(describe "compile"
  (before (test-fs/create-from test-page))

  (it "runs"
    (should-contain "<h1 data-id=\"title\">Test</h1>"(compiled))))
