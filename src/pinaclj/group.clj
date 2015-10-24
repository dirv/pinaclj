(ns pinaclj.group
  (:require [pinaclj.page :as page]))

(defn- tag-group-fn [page]
  (page/retrieve-value page :tags))

(defn- category-group-fn [page]
  [(page/retrieve-value page :category)])

(defn- group-page [page groups-fn]
  (let [groups (groups-fn page)]
    (if (empty? groups)
      {}
      (into {} (map #(vector (keyword %) [page]) groups)))))

(defn- pages-by-group [pages group-fn]
  (apply merge-with concat (map #(group-page % group-fn) pages)))

(defn pages-by-tag [pages] (pages-by-group pages tag-group-fn))
(defn pages-by-category [pages] (pages-by-group pages category-group-fn))
