(ns pinaclj.site-spec
  (require [speclj.core :refer :all]
           [pinaclj.site :refer :all]
           [pinaclj.date-time-helpers :as dt]))

(defn- title-writing-template [x]
  (apply str (map :title (:pages x))))

(def test-theme
  {:post.html {:template-func title-writing-template}
   :index.html {:template-func title-writing-template}})

(def base-page
  {:modified 2
   :path "/test.md"
   :published-at (dt/make 2015 1 1 1 1 1)
   :title "test"})

(defn- output-with-theme [pages theme]
  (build pages theme 1))

(defn- files-output [pages]
  (map first (output-with-theme pages test-theme)))

(defn- in [page-name pages]
  (second (first (filter #(= page-name (first %)) pages))))

(describe "build"

  (describe "new page"
    (def new-page (assoc base-page
                         :destination "new.html"
                         :title "new-page"))

    (it "writes page"
      (should-contain "new.html" (files-output [new-page])))
    (it "writes to index"
      (should-contain "new-page" (in "index.html"
                                     (output-with-theme [new-page] test-theme)))))

  (describe "old page"
    (def old-page (assoc base-page
                         :modified 1
                         :destination "old.html"
                         :title "old-page"))

    (it "does not write page"
      (should-not-contain "old.html" (files-output [old-page])))
    (it "writes to index"
      (should-contain "old-page" (in "index.html"
                                     (output-with-theme [old-page] test-theme)))))

  (describe "draft page"
    (def draft-page {:destination "draft.html" :title "draft"})

    (it "does not write page"
      (should-not-contain "draft.html" (files-output [draft-page])))
    (it "does not write to index"
      (should-not-contain "draft" (in "index.html"
                                      (output-with-theme [draft-page] test-theme)))))

  (describe "tag page"
    (def tag-page (assoc base-page
                         :tags '("tagA" "tagB" "tagC")
                         :destination "test.html"))

    (it "creates tag pages"
      (let [pages (files-output [tag-page])]
        (should-contain "tags/tagA/index.html" pages)
        (should-contain "tags/tagB/index.html" pages)
        (should-contain "tags/tagC/index.html" pages))))

  (describe "split page list"
    (def theme-with-max-page
      (assoc-in test-theme [:index.html :max-pages] 2))

    (def five-pages
      (map #(assoc base-page :title %) (range 1 6)))

    (defn- build-split-index []
      (output-with-theme five-pages theme-with-max-page))

    (it "splits index page"
      (should-contain "index.html" (map first (build-split-index)))
      (should-contain "index-2.html" (map first (build-split-index))))

    (it "outputs correct pages in split"
      (should= "12" (in "index.html" (build-split-index)))
      (should= "34" (in "index-2.html" (build-split-index)))
      (should= "5" (in "index-3.html" (build-split-index))))))
