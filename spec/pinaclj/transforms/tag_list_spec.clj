(ns pinaclj.transforms.tag-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.tag-list :refer :all]))

(def page
  {:tags '("a" "b" "c")})

(describe "get-tags"
  (it "returns titles"
    (should= '("a" "b" "c") (map :title (:pages (get-tags page {})))))
  (it "returns urls"
    (should-contain "/tags/a/" (map :url (:pages (get-tags page {}))))))
