(ns pinaclj.transforms.next-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.next :refer :all]))

(def a {:destination "a" :parent "parent"})
(def b {:destination "b" :parent "parent"})

(def parent-page
  {:destination "parent"
   :page-list ["a" "b"]})

(defn- build-page-map [pages]
  (apply merge (map #(hash-map (:destination %) %) pages)))

(def page-map
  {:all-pages (build-page-map [a b parent-page])})

(describe "choose-next"
  (it "is nil when last page"
    (should= nil (choose-next b page-map)))
  (it "chooses next page"
    (should= {:attrs {:href "b"}} (choose-next a page-map))))
