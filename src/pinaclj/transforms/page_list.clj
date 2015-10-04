(ns pinaclj.transforms.page-list
  (:require [pinaclj.page :as page]))

(defn- max-pages [{max-pages :max-pages}]
  (when-not (nil? max-pages)
    (Integer/parseInt max-pages)))

(defn- filter-all [{all-pages :all-pages category :category}]
  (keys (filter #(= category (page/retrieve-value (val %) :category {}))
          all-pages)))

(defn- filter-pages [page-set opts]
  (if (contains? opts :category)
    (filter-all opts)
    (:pages page-set)))

(defn- apply-max-pages [pages opts]
  (if-let [max-pages (max-pages opts)]
    (take max-pages pages)
    pages))

(defn- sort-pages [page-urls order-key all-pages reverse?]
  (page/to-page-urls
    (let [ordered (sort-by order-key (map #(get all-pages %) page-urls))]
      (if reverse?
        (reverse ordered)
        ordered))))

(defn- apply-order [pages {order-by :order-by all-pages :all-pages reverse? :reverse}]
  (if (nil? order-by)
    pages
    (sort-pages pages (keyword order-by) all-pages reverse?)))

(defn- find-pages [page-set opts]
  (-> page-set
      (filter-pages opts)
      (apply-order opts)
      (apply-max-pages opts)))

(defn clone-pages [page-set opts]
  {:pages (find-pages page-set opts)})

(def transform [:page-list clone-pages])
