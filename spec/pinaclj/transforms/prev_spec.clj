(ns pinaclj.transforms.prev-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.prev :refer :all]))

(def a {:destination "a" :parent "parent"})
(def b {:destination "b" :parent "parent"})

(def parent-page
  {:destination "parent"
   :page-list ["a" "b"]})

(defn- build-page-map [pages]
  (apply merge (map #(hash-map (:destination %) %) pages)))

(def page-map
  {:all-pages (build-page-map [a b parent-page])})

(describe "choose-prev"
  (it "is nil when first page"
    (should= nil (choose-prev a page-map)))
  (it "chooses previous page"
    (should= {:attrs {:href "a"}} (choose-prev b page-map))))
