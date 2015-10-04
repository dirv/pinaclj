(ns pinaclj.transforms.page-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.page-list :refer :all]))

(def all-pages
  {:url1 {:published-at 1 :title "c"}
   :url2 {:published-at 2 :title "b"}
   :url3 {:published-at 3 :title "a"}})

(def all-pages-opts
  {:all-pages all-pages})

(def pages
  {:pages [:url1 :url2 :url3]})

(def some-pages
  {:pages [:url2 :url3]})

(def generated-opts
  {:all-pages {:url1 {:published-at 4 :title "gen" :generated true}}})

(def category-pages
  {"a" {:category "cat" :destination "a"}
   "b" {:category "cat" :destination "b"}
   "c" {:destination "c"}})

(def category-opts
  {:all-pages category-pages
   :category "cat"})

(def index-page
  {:pages ["a" "b" "c"]})

(describe "clone-pages"
  (it "does not order provided page list"
    (should= pages (clone-pages pages all-pages-opts)))
  (it "lists only subset if page specificiations is given"
    (should= some-pages (clone-pages some-pages all-pages-opts)))
  (it "does not include generated pages"
    (should= 0 (count (:pages (clone-pages {} generated-opts)))))
  (it "filters all-pages when category opt is included"
    (should= ["a" "b"] (:pages (clone-pages index-page category-opts)))))
