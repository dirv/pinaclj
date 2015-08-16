(ns pinaclj.tag-page
  (:require [pinaclj.page :as page]))

(defn tag-url [tag]
  (str "tags/" (name tag) "/"))

(defn- tags [page]
  (page/retrieve-value page :tags {}))

(defn- page-by-tag [page]
  (let [tags (tags page)]
    (if-not (empty? tags)
      (apply assoc {} (mapcat #(list (keyword %) [page]) tags))
      {})))

(defn pages-by-tag [pages]
  (apply merge-with concat (map page-by-tag pages)))
