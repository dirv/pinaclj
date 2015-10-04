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

(defn- find-pages [page-set opts]
  (let [pages (filter-pages page-set opts)
        max-pages (max-pages opts)]
    (if (nil? max-pages)
      pages
      (take max-pages pages))))

(defn clone-pages [page-set opts]
  {:pages (find-pages page-set opts)})

(def transform [:page-list clone-pages])
