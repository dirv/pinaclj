(ns pinaclj.core-spec
  (:require [pinaclj.core :refer :all]
            [pinaclj.files :as files]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.templates :as templates]
            [speclj.core :refer :all]))

(def nested-page
   {:path "pages/nested/another_post.md"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n\ncontent\n" })

(def simple-page
  {:path "pages/post.md"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n\n###Markdown header\nMarkdown paragraph."})

(def draft-page
  {:path "pages/a-draft.md"
   :content "title: Not sure yet\n\nDraft post"})

(def all-pages
  [nested-page simple-page draft-page])

(defn- compile-page [fs]
  (compile-all (files/resolve-path fs "pages")
               (files/resolve-path fs "published") templates/page))

(describe "compile-all"
  (with fs (test-fs/create-from all-pages))

  (before
    (compile-page @fs))

  (it "creates the file"
    (should (files/exists? (files/resolve-path @fs "published/post.html"))))

  (it "renders the title"
    (should-contain "<h1 data-id=\"title\">Test</h1>"
                    (files/content (files/resolve-path @fs "published/post.html"))))

  (it "renders the content without escaping"
    (should-contain "<h3>Markdown header</h3>"
                    (files/content (files/resolve-path @fs "published/post.html"))))

  (it "compiles files in subdirectories"
    (should (files/exists? (files/resolve-path @fs "published/nested/another_post.html"))))

  (it "does not publish drafts"
    (should-not (files/exists? (files/resolve-path @fs "published/a-draft.html")))))
