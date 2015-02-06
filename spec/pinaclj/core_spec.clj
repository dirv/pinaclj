(ns pinaclj.core-spec
  (:require [pinaclj.core :refer :all]
            [pinaclj.files :as files]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.templates :as templates]
            [speclj.core :refer :all]))

(def nested-page
   {:path "pages/nested/another_post.md"
    :content "title: Nested Title\npublished-at: 2014-10-31T10:05:00Z\n---\ncontent\n" })

(def simple-page
  {:path "pages/post.md"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n---\n###Markdown header\nMarkdown paragraph."})

(def draft-page
  {:path "pages/a-draft.md"
   :content "title: Not sure yet\n---\nDraft post"})

(def url-page
  {:path "pages/a-test-path.md"
   :content "url: /a/blog/page.html\npublished-at: 2014-10-31T10:05:00Z\n---\nContent"})

(def url-index-page
  {:path "pages/a-wordpress-style-path.md"
   :content "url: /a/blog/page/\npublished-at: 2014-10-31T10:05:00Z\n---\nContent"})

(def published-pages
  [nested-page simple-page url-page url-index-page])

(def all-pages
  [nested-page simple-page draft-page url-page url-index-page])

(defn- compile-page [fs]
  (compile-all (files/resolve-path fs "pages")
               (files/resolve-path fs "published") (templates/build-page-func "templates/page.html")))

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
    (should-not (files/exists? (files/resolve-path @fs "published/a-draft.html"))))

  (it "uses the url header if one is present"
    (should (files/exists? (files/resolve-path @fs "published/a/blog/page.html"))))

  (it "adds html extension if it isn't present"
    (should (files/exists? (files/resolve-path @fs "published/a/blog/page/index.html")))))


(defn render-page-list []
  (apply str (render-single (compile-page @fs) (templates/build-list-func "templates/page_list.html"))))

(describe "render-single"
  (with fs (test-fs/create-from published-pages))

  (it "renders right number of non-draft pages"
    (should= (count published-pages) (count (re-seq #"<a" (render-page-list)))))

  (it "renders page title"
    (println (render-page-list))
    (should-contain "Nested Title" (render-page-list))))

