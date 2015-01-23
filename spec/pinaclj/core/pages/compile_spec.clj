(ns pinaclj.core.pages.compile-spec
  (:require [pinaclj.core.pages.compile :refer :all]
            [pinaclj.core.files :as files]
            [pinaclj.core.test-fs :as test-fs]
            [pinaclj.core.templates :as templates]
            [speclj.core :refer :all]))

(def nested-page
   {:path "drafts/nested/another_post.md"
    :content "title: Test\n\ncontent"})

(def simple-page
  {:path "drafts/post.md"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n\n###Markdown header\nMarkdown paragraph."})

(defn- compile-page [fs page]
  (compile-all (files/resolve-path fs "drafts")
               (files/resolve-path fs "published") templates/page))

(describe "compile-all"
  (with fs (test-fs/create-from [simple-page nested-page]))

  (it "creates the file"
    (compile-page @fs simple-page)
    (should (files/exists? (files/resolve-path @fs "published/post.html"))))

  (it "renders the title"
    (compile-page @fs simple-page)
    (should-contain "<h1 data-id=\"title\">Test</h1>"
                    (files/content (files/resolve-path @fs "published/post.html"))))

  (it "renders the content without escaping"
    (compile-page @fs simple-page)
    (should-contain "<h3>Markdown header</h3>"
                    (files/content (files/resolve-path @fs "published/post.html"))))

  (it "compiles files in subdirectories"
    (compile-page @fs nested-page)
    (should (files/exists? (files/resolve-path @fs "published/nested/another_post.html")))))
