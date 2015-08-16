(ns pinaclj.transforms.tag-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.tag-page :as tp]
           [pinaclj.transforms.tag-list :refer :all]))

(def tag-page
  {:tags '(:tagA :tagB)})

(def tag-page-A
  {:title "tagA"
   :destination (tp/tag-url :tagA)})

(describe "get-tags"
  (it "returns right number of tag pages"
    (should= 2 (count (:pages (get-tags tag-page {})))))
  (it "returns correct page"
    (should= tag-page-A (first (:pages (get-tags tag-page {}))))))

