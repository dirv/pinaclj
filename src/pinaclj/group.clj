(ns pinaclj.group
  (:require [pinaclj.page :as page]))

(def uncategorized
  :uncategorized)

(defn category-url [category]
  (str "category/" (name category) "/"))

(defn tag-url [tag]
  (str "tags/" (name tag) "/"))

(defn- tag-group-fn [page]
  (page/retrieve-value page :tags {}))

(defn- category-group-fn [page]
  (if-let [category (page/retrieve-value page :category {})]
    [category]
    [uncategorized]))

(defn- group-page [page groups-fn]
  (let [groups (groups-fn page)]
    (if (empty? groups)
      {}
      (apply assoc {} (mapcat #(list (keyword %) [page]) groups)))))

(defn- pages-by-group [pages group-fn]
  (apply merge-with concat (map #(group-page % group-fn) pages)))

(defn pages-by-tag [pages] (pages-by-group pages tag-group-fn))
(defn pages-by-category [pages] (pages-by-group pages category-group-fn))
