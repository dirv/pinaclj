(ns pinaclj.transforms.category-link-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.category-link :refer :all]))

(def page
  {:category :cat})

(def cat
  {:title "cat"
   :destination "category/cat/" })

(describe "get-category"
  (it "returns the category page"
    (should= cat (:page (get-category page {})))))
