(ns pinaclj.tag-page-spec
  (require [speclj.core :refer :all]
           [pinaclj.tag-page :refer :all]))

(def pageA
  {:tags ["tagA" "tagB" "tagC"]})

(def pageB
  {:tags ["tagA" "tagB"]})

(def pageC
  {:tags []})

(def all-pages
  [pageA pageB pageC])

(describe "pages-by-tag"
  (it "keys by distinct tags"
    (should== '(:tagA :tagB :tagC) (keys (pages-by-tag all-pages))))

  (it "includes all pages in tag value"
    (should-contain pageA (:tagA (pages-by-tag all-pages)))
    (should-contain pageB (:tagA (pages-by-tag all-pages)))))
