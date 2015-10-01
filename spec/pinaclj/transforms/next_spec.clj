(ns pinaclj.transforms.next-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.next :refer :all]))

(def a {:destination "a" :parent "parent" :title "title A"})
(def b {:destination "b" :parent "parent" :title "title B" :next "a"})
(def c {:destination "c" :parent "parent" :title "title C"})

(def parent-page
  {:destination "parent"
   :pages ["a" "b" "c"]})

(defn- build-page-map [pages]
  (apply merge (map #(hash-map (:destination %) %) pages)))

(def page-map
  {:all-pages (build-page-map [a b parent-page])})

(describe "choose-next"
  (it "is nil when last page"
    (should= nil (choose-next c page-map)))
  (it "sets href of next page"
    (should= "b" (:href (:attrs (choose-next a page-map)))))
  (it "sets content to title of next page"
    (should= "title B" (:content (choose-next a page-map))))
  (it "uses :next key value if set"
    (should= "a" (:href (:attrs (choose-next b page-map))))))
