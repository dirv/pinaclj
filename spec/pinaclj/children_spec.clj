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

(describe "children"
  (context "with no options"
    (it "returns no pages if none specified"
      (should= [] (children {} {} [])))
    (it "returns all pages"
      (should== ["a" "b" "c" "d"] (children {} {} all-pages)))
    (it "returns pages in reverse chronological order"
      (should= ["b" "a" "c" "d"] (children {} {} all-pages))))
  (context "with category"
    (it "returns pages in that category"
      (should= ["d"] (children {} {:category "non-default"} all-pages))))
  (context "with parent category"
    (it "returns contacts with that header set"
      (should== ["a" "c"] (children filter-parent {} filter-pages)))
    (it "does not filter if category is set"
      (should= ["d"] (children filter-parent {:category "non-default"} all-pages)))
    (it "does not filter if category is default-category"
      (should== ["a" "b" "c"] (children default-category-filter {} filter-pages))))
  (context "with order by"
    (it "orders according to :order key"
      (should= ["b" "c" "a"] (children {} ordered-opts ordered-pages)))
    (it "reverses if :reverse is set"
      (should= ["a" "c" "b"] (children {} reverse-ordered-opts ordered-pages)))))
