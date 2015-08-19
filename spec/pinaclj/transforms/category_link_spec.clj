(ns pinaclj.transforms.category-link-spec
  (:require [speclj.core :refer :all]
            [pinaclj.group :as group]
            [pinaclj.transforms.category-link :refer :all]))

(def page
  {:category :cat})

(def cat-link
  {:content "cat"
   :attrs {:href (group/category-url :cat)} })

(describe "get-category"
  (it "returns the category link"
    (should= cat-link (get-category page {}))))
