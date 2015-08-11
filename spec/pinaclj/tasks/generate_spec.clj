(ns pinaclj.tasks.generate-spec
  (:require [pinaclj.tasks.generate :refer :all]
            [pinaclj.test-fs :refer :all]
            [pinaclj.test-templates :as test-templates]
            [speclj.core :refer :all]
            [pinaclj.translate :refer :all]))

(def nested-page
  {:path "pages/nested/another_post.md"
   :content "title: Nested Title\npublished-at: 2014-10-31T11:05:00Z\n---\ncontent\n"})

(def simple-page
  {:path "pages/post.md"
    :content "title: Test\npublished-at: 2014-10-31T12:05:00Z\n---\ncontent"})

(defn publish-message-for [page]
  (t :en :generate/published-page page))

(describe "generate"
  (with fs (create-from [nested-page simple-page]))

  (before (test-templates/write-to-fs @fs))

  (describe "file results"
    (before (generate @fs "pages" "published" "theme"))

    (it "creates simple post"
      (should (file-exists? @fs "published/post.html")))

    (it "compiles files in subdirectories"
      (should (file-exists? @fs "published/nested/another_post.html"))))

  (describe "task output"
    (it "outputs successful pages"
      (let [output (generate @fs "pages" "published" "theme")
            messages (map :msg output)]
        (should= 4 (count (filter #(= :success (:type %)) output)))
        (should-contain (publish-message-for "index.html") messages)
        (should-contain (publish-message-for "feed.xml") messages)
        (should-contain (publish-message-for "post.html") messages)
        (should-contain (publish-message-for "nested/another_post.html") messages)))))
