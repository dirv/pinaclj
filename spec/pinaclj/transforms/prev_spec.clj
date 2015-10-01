(ns pinaclj.transforms.prev-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.prev :refer :all]))

(def a {:destination "a" :parent "parent" :title "title A"})
(def b {:destination "b" :parent "parent" })
(def c {:destination "c" :parent "parent" :prev "a"})
(def d {:destination "d" :parent "parent" :prev nil})

(def parent-page
  {:destination "parent"
   :pages ["a" "b" "c" "d"]})

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
    (should= "title A" (:content (choose-prev b page-map))))
  (it "uses :prev key value if set"
    (should= "a" (:href (:attrs (choose-prev c page-map)))))
  (it "uses :prev value of nil if explicitly set"
    (should= nil (choose-prev d page-map))))
