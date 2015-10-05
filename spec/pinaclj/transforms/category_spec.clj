(ns pinaclj.transforms.category-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.category :refer :all]))

(def no-category {:title "none"})

(describe "convert-category"
  (it "creates default category"
    (should= default-category (convert-category [ no-category ] {}))))

