(ns pinaclj.core-spec
  (:require [pinaclj.core :refer :all]
            [pinaclj.test-fs :refer :all]
            [pinaclj.test-templates :as test-templates]
            [speclj.core :refer :all]))

(def nested-page
  {:path "pages/nested/another_post.md"
   :content "title: Nested Title\npublished-at: 2014-10-31T11:05:00Z\n---\ncontent\n"})

(def simple-page
  {:path "pages/post.md"
    :content "title: Test\npublished-at: 2014-10-31T12:05:00Z\n---\ncontent"})

(describe "compile-all"
  (with fs (create-from [nested-page simple-page]))

  (before (test-templates/write-to-fs @fs))

  (before (compile-all @fs "pages" "published" "theme"))

  (it "creates simple post"
    (should (file-exists? @fs "published/post.html")))

  (it "compiles files in subdirectories"
    (should (file-exists? @fs "published/nested/another_post.html"))))
