(ns pinaclj.transforms.prev-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.prev :refer :all]))

(def a {:destination "a" :parent "parent" :title "title A"})
(def b {:destination "b" :parent "parent" :title "title B"})

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
  (it "sets href of prev page"
    (should= "a" (:href (:attrs (choose-prev b page-map)))))
  (it "sets content to title of prev page"
    (should= "title A" (:content (choose-prev b page-map)))))
