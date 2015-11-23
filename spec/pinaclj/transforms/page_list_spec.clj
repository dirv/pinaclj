(ns pinaclj.transforms.page-list-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.category :as category]
            [pinaclj.transforms.page-list :refer :all]))

(defn- build-page-map [pages]
  (into {} (map #(vector (:destination %) %) pages)))

(def page-map
  [{:published-at 1 :title "c" :destination "url1" :category category/default-category}
   {:published-at 2 :title "b" :destination "url2" :category category/default-category}
   {:published-at 3 :title "a" :destination "url3" :category category/default-category}
   {:published-at 4 :title "d" :destination "url4" :category :another}])

(def page-map-opts {:all-pages (build-page-map page-map)})

(def all-pages {:pages ["url1" "url2" "url3" "url4"]})
(def some-pages {:pages ["url2" "url3"]})

(def max-page-opts
  (assoc page-map-opts :max-pages "2"))

(describe "clone-pages"
  (it "provides no child pages if no :pages set"
    (should== [] (:pages (clone-pages {} page-map-opts))))
  (it "uses :pages if :max-pages set"
    (should== ["url2" "url3"] (:pages (clone-pages some-pages max-page-opts))))
  (it "restricts to max-pages if set"
    (should= 2 (count (:pages (clone-pages all-pages max-page-opts))))))
