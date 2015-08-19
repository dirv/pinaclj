(ns pinaclj.group-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.category :as category]
            [pinaclj.group :refer :all]))

(def tag-page-a {:tags ["tagA" "tagB" "tagC"]})
(def tag-page-b {:tags ["tagA" "tagB"]})
(def tag-page-c {:tags []})

(def all-pages [tag-page-a tag-page-b tag-page-c])

(describe "pages-by-group"
  (it "keys by distinct tags"
    (should== '(:tagA :tagB :tagC) (keys (pages-by-tag all-pages))))

  (it "includes all pages in tag value"
    (should-contain tag-page-a (:tagA (pages-by-tag all-pages)))
    (should-contain tag-page-b (:tagA (pages-by-tag all-pages)))))

(def category-page-a {:category :a :title "a"})
(def category-page-b {:category :a :title "b"})
(def category-page-c {:category :c :title "c"})
(def all-category-pages [category-page-a category-page-b category-page-c])

(describe "pages-by-category"
  (it "keys by distinct categories"
    (should== '(:a :c) (keys (pages-by-category all-category-pages))))
  (it "includes all pages in category value"
    (should-contain category-page-a (:a (pages-by-category all-category-pages)))
    (should-contain category-page-c (:c (pages-by-category all-category-pages)))))
