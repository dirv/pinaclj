(ns pinaclj.transforms.page-list
  (:require [pinaclj.page :as page]))

(defn- max-pages [{max-pages :max-pages}]
  (when-not (nil? max-pages)
    (Integer/parseInt max-pages)))

(defn- chronological-sort [pages]
  (reverse (sort-by :published-at pages)))

(defn- clone-pages [page-set opts]
  (let [children (chronological-sort (:pages page-set))
        max-pages (max-pages opts)]
    (if (nil? max-pages)
     {:pages children}
     {:pages (take max-pages children)})))

(defn apply-transform [page-set]
  (page/set-lazy-value page-set :page-list clone-pages))
