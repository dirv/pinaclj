(ns pinaclj.children-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.category :as category]
            [pinaclj.children :refer :all]))

(def default-category category/default-category)

(def all-pages
  [{:destination "a" :published-at 2 :category default-category}
   {:destination "b" :published-at 3 :category default-category}
   {:destination "c" :published-at 1 :category default-category}
   {:destination "d" :category :non-default}
   ])

(def generated-page
  {:destination "g" :generated true :category default-category})

(def filter-pages
  [{:destination "a" :author "wayne" :category default-category}
   {:destination "b" :author "garth" :category default-category}
   {:destination "c" :author "wayne" :category default-category}])

(def filter-parent
  {:title "wayne" :category :author})

(def default-category-filter
  {:title "me" :category default-category})
(def ordered-pages
  [{:destination "a" :order 3 :category default-category}
   {:destination "b" :order 1 :category default-category}
   {:destination "c" :order 2 :category default-category}])

(def ordered-opts {:order-by "order"})
(def reverse-ordered-opts (assoc ordered-opts :reverse "true"))

(defn- build-opts [pages]
  (into {} (map #(vector (:destination %) %) pages)))

(describe "children"
  (describe "with no options"
    (it "returns no pages in none specified"
      (should= [] (children {} {} (build-opts []))))
    (it "returns all pages"
      (should== ["a" "b" "c" "d"] (children {} {} (build-opts all-pages))))
    (it "returns pages in reverse chronological order"
      (should= ["b" "a" "c" "d"] (children {} {} (build-opts all-pages))))
    (it "removes generated pages"
      (should= [] (children {} {} (build-opts [generated-page])))))
  (describe "with category"
    (it "returns pages in that category"
      (should= ["d"] (children {} {:category "non-default"} (build-opts all-pages)))))
  (describe "with parent category"
    (it "returns contacts with that header set"
      (should== ["a" "c"] (children filter-parent {} (build-opts filter-pages))))
    (it "does not filter if category is set"
      (should= ["d"] (children filter-parent {:category "non-default"} (build-opts all-pages))))
    (it "does not filter if category is default-category"
      (should== ["a" "b" "c"] (children default-category-filter {} (build-opts filter-pages)))))
  (describe "with order by"
    (it "orders according to :order key"
      (should= ["b" "c" "a"] (children {} ordered-opts (build-opts ordered-pages))))
    (it "reverses if :reverse is set"
      (should= ["a" "c" "b"] (children {} reverse-ordered-opts (build-opts ordered-pages))))))
