(ns pinaclj.children
  (:require [pinaclj.page :as page]
            [pinaclj.transforms.category :as category]))

(defn- filter-to-category [all-pages category]
  (filter #(= (keyword category) (page/retrieve-value % :category)) all-pages))

(defn- sort-pages [all-pages order-by reverse?]
  (let [sorted (vec (sort-by #(page/retrieve-value % order-by) all-pages))]
    (if reverse? (rseq sorted) sorted)))

(defn- matches? [k v page]
  (= v (page/retrieve-value page k)))

(defn- filter-to-parent [pages parent]
  (let [parent-category (keyword (page/retrieve-value parent :category))
        title (page/retrieve-value parent :title)]
    (if (= category/default-category parent-category)
      pages
      (filter #(matches? parent-category title %) pages))))

(defn- filter-to-parent-or-category [pages parent {category :category}]
  (if category
    (filter-to-category pages category)
    (filter-to-parent pages parent)))

(defn- order-key [{order-key :order-by}]
  (if order-key (keyword order-key) :published-at))

(defn- reverse? [{order-key :order-by reverse? :reverse}]
  (or (nil? order-key) reverse?))

(defn- to-urls [pages]
  (map #(page/retrieve-value % :destination) pages))

(defn children [parent list-node-attrs all-pages]
  (-> all-pages
      (filter-to-parent-or-category parent list-node-attrs)
      (sort-pages (order-key list-node-attrs) (reverse? list-node-attrs))
      (to-urls)))
