(ns pinaclj.transforms.category-link-spec
  (:require [speclj.core :refer :all]
            [pinaclj.group :as group]
            [pinaclj.transforms.category-link :refer :all]))

(def page
  {:category :cat})

(def cat-link
  {:title "cat"
   :destination (group/category-url :cat) })

(describe "get-category"
  (it "returns the category page"
    (should= (:title cat-link) (:title (:page (get-category page {})))))
  (it "applies transforms"
    (should-contain :funcs (:page (get-category page {})))))
