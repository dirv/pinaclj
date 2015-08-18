(ns pinaclj.category-page-spec
  (:require [speclj.core :refer :all]
            [pinaclj.category-page :refer :all]))

(def pageA {:category :a :title "a"})
(def pageB {:category :a :title "b"})
(def pageC {:category :c :title "c"})
(def all-pages [pageA pageB pageC])

(describe "pages-by-category"
  (it "keys by distinct categories"
    (should== '(:a :c) (keys (pages-by-category all-pages))))
  (it "includes all pages in category value"
    (should-contain pageA (:a (pages-by-category all-pages)))
    (should-contain pageC (:c (pages-by-category all-pages))))
  (it "creates uncategorized"
    (def no-category {:title "none"})
    (should= [uncategorized] (keys (pages-by-category [ no-category ])))))
