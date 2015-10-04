(ns pinaclj.transforms.page-link-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.page-link :refer :all]))

(def author-page
  {:title "wayne" :destination "authors/wayne.html"})

(def page-with-author
  {:author "wayne" :destination "child.html" :title "child"})

(defn- build-page-map [pages]
  (apply merge (map #(hash-map (:destination %) %) pages)))

(def all-pages
  (build-page-map [author-page page-with-author]))

(describe "set-page-link"
  (it "sets according to key"
    (let [link (set-page-link page-with-author {:all-pages all-pages
                                                :key "author"})]
      (should= "authors/wayne.html" (get-in link [:attrs :href]))
      (should= "wayne" (:content link)))))
