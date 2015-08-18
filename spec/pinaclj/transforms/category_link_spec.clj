(ns pinaclj.transforms.category-link-spec
  (:require [speclj.core :refer :all]
            [pinaclj.category-page :as cp]
            [pinaclj.transforms.category-link :refer :all]))

(def page
  {:category :cat})

(def cat-link
  {:title "cat"
   :destination (cp/category-url :cat) })

(describe "get-category"
  (it "returns the category page"
    (should= cat-link (:page (get-category page {})))))
