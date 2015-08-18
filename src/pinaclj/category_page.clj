(ns pinaclj.category-page
  (:require [pinaclj.page :as page]))

(def uncategorized
  :uncategorized)

(defn category-url [category]
  (str "category/" (name category) "/"))

(defn- category [page]
  (page/retrieve-value page :category {}))

(defn- page-by-category [page]
  (let [category (category page)]
    (if-not (nil? category)
      {category [page]}
      {uncategorized [page]})))

(defn pages-by-category [pages]
  (apply merge-with concat (map page-by-category pages)))
