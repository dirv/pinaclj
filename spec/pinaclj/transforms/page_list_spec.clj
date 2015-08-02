(ns pinaclj.transforms.page-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.page-list :refer :all]))

(def pages
  {:pages '({:published-at 1 :title "c"}
            {:published-at 2 :title "b"}
            {:published-at 3 :title "a"})})

(describe "clone-pages"
  (it "orders pages in reverse chronological order"
    (should= '("a" "b" "c") (map :title (:pages (clone-pages pages {}))))))
