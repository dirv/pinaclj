(ns pinaclj.page-builder-spec
  (:require [speclj.core :refer :all]
            [pinaclj.page-builder :refer :all]))

(def page-a {:destination :urlA :title "a" :tags ["test"] :published-at 5})
(def page-b {:destination :urlB :title "b" :tags ["test"] :published-at 4})
(def page-c {:destination :urlC :title "c" :tags ["test"] :published-at 3})
(def page-d {:destination :urlC :title "d" :tags ["test"] :published-at 2})
(def page-e {:destination :urlC :title "e" :tags ["test"] :published-at 1})

(def tag-pages [page-a page-b page-c])

(def all-pages
  {:urlA page-a
   :urlB page-b
   :urlC page-c
   :urlD page-d
   :urlE page-e})

(def list-page
  (assoc (generate-page "index.html")
         :pages [:urlA :urlB :urlC :urlD :urlE]))

(def tag-page
  (first (build-tag-pages [page-a page-b page-c])))

(def path-only-page
  (assoc (create-page nil "path.md")
         :pages [:urlA :urlB :urlC :urlD :urlE]))

(def template-with-no-max
  nil)

(def template-with-low-max
  {:max-pages 2})

(def template-with-high-max
  {:max-pages 5})

(defn- divide-pages []
  (divide list-page template-with-low-max all-pages))

(describe "divide"
  (it "does not divide pages without max-pages set"
    (should= [list-page] (divide list-page template-with-no-max all-pages)))
  (it "does not divide pages which does not hit max-pages"
    (should= 1 (count (divide list-page template-with-high-max all-pages))))
  (it "divides pages with low max-pages"
    (should= 3 (count (divide-pages))))

  (it "contains pages in order when dividing pages"
    (should= [[:urlA :urlB] [:urlC :urlD] [:urlE]] (map :pages (divide-pages))))
  (it "sets start page when dividing"
    (should= [0 2 4] (map :start (divide-pages))))
  (it "modifies all but first urls"
    (should= ["index.html" "index-2.html" "index-3.html"] (map :url (divide-pages))))
  (it "does not add next link to first page"
    (should= [nil "index.html" "index-2.html"] (map :next (divide-pages))))
  (it "does not add prev link to last page"
    (should= ["index-2.html" "index-3.html" nil] (map :prev (divide-pages))))
  (it "sets url using correct definition"
    (should= ["tag/test/index.html" "tag/test/index-2.html"]
      (map :url (divide tag-page template-with-low-max all-pages))))
  (it "can use file path as base url"
    (should= ["path.html" "path-2.html" "path-3.html"]
      (map :url (divide path-only-page template-with-low-max all-pages)))))

(describe "build-tag-pages"
  (it "builds"
    (should= 1 (count (build-tag-pages tag-pages)))
    (should== [:urlA :urlB :urlC] (:pages (first (build-tag-pages tag-pages))))
    (should= '("test") (map :title (build-tag-pages tag-pages))))
  (it "sets parent page as index"
    (should= "index.html" (:parent (first (build-tag-pages tag-pages))))))

(def cat-page-a {:category :a :title "a" :published-at 2 :destination "a"})
(def cat-page-b {:category :uncategorized :title "b" :destination "b"})
(def cat-page-c {:category :a :title "c" :published-at 1 :destination "c"})
(def cat-pages [cat-page-a cat-page-b cat-page-c])

(describe "build-category-pages"
  (it "builds all pages"
    (should= 2 (count (build-category-pages cat-pages)))
    (should= '("a" "uncategorized") (map :title (build-category-pages cat-pages))))
  (it "sets parent page as index"
    (should= "index.html" (:parent (first (build-category-pages cat-pages)))))
  (it "orders category pages in chronological order"
    (should= ["c" "a"] (:pages (first (build-category-pages cat-pages))))))
