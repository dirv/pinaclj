(ns pinaclj.children
  (:require [pinaclj.page :as page]
            [pinaclj.transforms.category :as category]))

(defn- filter-to-category [all-pages category]
  (filter #(= (keyword category) (page/retrieve-value (val %) :category {})) all-pages))

(defn- sort-pages [all-pages order-by reverse?]
  (let [sorted (sort-by #(get (val %) order-by) all-pages)]
    (if reverse? (reverse sorted) sorted)))

(defn- matches? [k v page]
  (= v (page/retrieve-value page k {})))

(defn- filter-to-parent [pages parent]
  (let [parent-category (keyword (page/retrieve-value parent :category {}))
        title (page/retrieve-value parent :title {})]
    (if (= category/default-category parent-category)
      pages
      (filter #(matches? parent-category title (val %)) pages))))

(defn- filter-to-parent-or-category [pages parent {category :category}]
  (if category
    (filter-to-category pages category)
    (filter-to-parent pages parent)))

(defn- order-key [{order-key :order-by}]
  (if order-key (keyword order-key) :published-at))

(defn- reverse? [{order-key :order-by reverse? :reverse}]
  (or (nil? order-key) reverse?))

(defn- remove-generated [pages]
  (remove #(:generated (val %)) pages))

(defn- to-urls [pages]
  (map key pages))

(defn children [page list-node-attrs all-pages]
  (-> all-pages
      (remove-generated)
      (filter-to-parent-or-category page list-node-attrs)
      (sort-pages (order-key list-node-attrs) (reverse? list-node-attrs))
      (to-urls)))
