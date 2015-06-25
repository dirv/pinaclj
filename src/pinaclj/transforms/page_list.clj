(ns pinaclj.transforms.page-list
  (:require [pinaclj.page :as page]
            [net.cgrand.enlive-html :as html]))

(defn- chronological-sort [pages]
  (reverse (sort-by :published-at pages)))

(defn- clone-pages [page-set opts]
  {:pages (chronological-sort (:pages page-set))})

(defn apply-transform [page-set]
  (page/set-lazy-value page-set :page-list clone-pages))
