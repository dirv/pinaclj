(ns pinaclj.site-spec
  (:require [speclj.core :refer :all]
            [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.transforms.templated-content :as content]
            [pinaclj.site :refer :all]
            [pinaclj.date-time-helpers :as dt]))

(defn- title-writing-template [all-pages]
  (fn [page]
    (:title page)))

(defn- child-writing-template [all-pages]
  (fn [page]
    (let [children (:pages page)
          titles (map #(:title (get all-pages %)) children)]
      (apply str titles))))

(def test-theme
  {:templates {:post.html {:template-fn (fn [pages] (title-writing-template pages))
                           :modified-at 0}
               :index.html {:template-fn (fn [pages] (child-writing-template pages))
                            :modified-at 0
                            :requires-split? true}}})

(def base-page
  {:modified 2
   :src-modified 2
   :category :post
   :path "/test.md"
   :published-at (dt/make 2015 1 1 1 1 1)
   :title "test"
   :funcs {:templated-content content/build}})

(defn- output-with-theme [pages theme]
  (build pages theme 1))

(defn- files-output [pages]
  (keys (output-with-theme pages test-theme)))

(defn- in [page-name pages]
  (val (first (filter #(= page-name (first %)) pages))))

(describe "new page"
  (def new-page (assoc base-page
                       :destination "new.html"
                       :title "new-page"))

  (it "writes page"
    (should-contain "new.html" (files-output [new-page]))
    (should-contain "new-page" (in "new.html" (output-with-theme [new-page] test-theme))))

  (it "writes index"
    (should-contain "index.html" (files-output [new-page]))
    (should-contain "new-page" (in "index.html"
                                   (output-with-theme [new-page] test-theme)))))

(describe "old page"
  (def old-page (assoc base-page
                       :modified 1
                       :destination "old.html"
                       :title "old-page"))

  (it "does not write page"
    (should-not-contain "old.html" (files-output [old-page])))
  (it "does not write index"
    (should-not-contain "index.html" (files-output [old-page]))))

(describe "draft page"
  (def draft-page {:destination "draft.html" :title "draft"})

  (it "does not write page"
    (should-not-contain "draft.html" (files-output [draft-page]))))

(describe "tag page"
  (def tag-page (assoc base-page
                       :tags '("tagA" "tagB" "tagC")
                       :destination "test.html"))

  (it "creates tag pages"
    (let [pages (files-output [tag-page])]
      (should-contain "tag/tagA/index.html" pages)
      (should-contain "tag/tagB/index.html" pages)
      (should-contain "tag/tagC/index.html" pages))))

(describe "category page"
  (def category-page (assoc base-page
                            :category :a))

  (def post-index-page (assoc base-page
                              :category nil
                              :destination "index.html"
                              :title "uncat-index"))

  (def categorized-index-page (assoc base-page
                                     :path "/index.md"
                                     :category "a"
                                     :destination "index.html"
                                     :title "cat-index"))

  (it "creates category pages"
    (should-contain "category/a/index.html" (files-output [category-page])))

  (it "does not create default category list"
    (should-not-contain "category/post/index.html" (files-output [categorized-index-page])))

  (it "includes index page if it has a category"
    (should-contain "cat-index"
      (in "category/a/index.html"
          (output-with-theme [categorized-index-page] test-theme)))))

(describe "split page list"
  (def theme-with-max-page
    (assoc-in test-theme [:templates :index.html :max-pages] 2))

  (def five-pages
    (map #(assoc base-page
                 :title %
                 :destination %) (range 1 6)))

  (def six-pages
    (conj five-pages (assoc base-page
                            :title 6
                            :destination 6
                            :modified 3)))

  (defn- build-split-index []
    (output-with-theme five-pages theme-with-max-page))

  (it "splits index page"
    (should-contain "index.html" (keys (build-split-index)))
    (should-contain "index-2.html" (keys (build-split-index)))
    (should-contain "index-3.html" (keys (build-split-index))))

  (it "outputs ordered pages in split"
    (should= "54" (in "index.html" (build-split-index)))
    (should= "32" (in "index-2.html" (build-split-index)))
    (should= "1" (in "index-3.html" (build-split-index))))

  (it "rewrites all split pages when one page is added"
    (let [page-urls (keys (build six-pages theme-with-max-page 2))]
      (should-contain "index.html" page-urls)
      (should-contain "index-2.html" page-urls)
      (should-contain "index-3.html" page-urls))))

(describe "source and theme matching"
  (defn- this-page-template [all-pages]
    (fn [page] (:title page)))

  (def match-theme
    {:templates {:post.html {:template-fn (fn [pages] (this-page-template pages))}
                 :index.html {:template-fn (fn [pages] (this-page-template pages))
                              :modified-at 1}}})

  (def index-page
    (assoc base-page
           :src-modified 2
           :title "test-title"
           :path "index.md"
           :destination "index.html"))

  (it "matches source file with theme file"
    (should= "test-title" (in "index.html" (output-with-theme [index-page] match-theme)))))

(def updated-template-theme
  (assoc-in test-theme [:templates :post.html :modified-at] 3))

(def unmodified-page
  (assoc base-page
         :modified 0
         :destination "page.html"
         :title "old-title"))

(describe "updated template file"
  (it "causes page to be rebuilt even when it hasn't changed"
    (should= "old-title" (in "page.html" (output-with-theme [unmodified-page] updated-template-theme)))))

(def file-only-template
  {:template-fn (fn [pages] (title-writing-template pages))
   :modified-at 3})

(def file-only-theme
  {:templates {:post.html file-only-template
               :index.html file-only-template
               :author.html file-only-template
               :random.html file-only-template}})

(def file-only-pages
  [(assoc base-page :category "author" :destination "wayne.html")
   (assoc base-page :destination "index.html")
   (assoc base-page :destination "standard.html")])

(describe "template"
  (it "writes unused template files"
    (let [pages (output-with-theme file-only-pages file-only-theme)]
      (should-contain "random.html" (map first pages))))
  (it "does not write used template files"
    (let [pages (output-with-theme file-only-pages file-only-theme)]
      (should-not-contain "post.html" (map first pages))
      (should-not-contain "author.html" (map first pages)))))
