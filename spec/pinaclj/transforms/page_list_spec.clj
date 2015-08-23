(ns pinaclj.transforms.page-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.page-list :refer :all]))

(def all-pages
  {:url1 {:published-at 1 :title "c"}
   :url2 {:published-at 2 :title "b"}
   :url3 {:published-at 3 :title "a"}})

(def pages
  {:pages [:url1 :url2 :url3]})

(describe "clone-pages"
  (it "orders pages in reverse chronological order"
    (should= '("a" "b" "c") (map :title (:pages (clone-pages pages {:all-pages all-pages}))))))
