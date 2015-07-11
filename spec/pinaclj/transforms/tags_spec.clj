(ns pinaclj.transforms.tags-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.tags :refer :all]))

(def page
  {:tags "tagA, tagB, tagC"})

(describe "get-tags"
  (it "splits tags"
    (should= '("tagA" "tagB" "tagC") (get-tags page {}))))
