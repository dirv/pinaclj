(ns pinaclj.transforms.modified
  (:require [pinaclj.page :as page]))

(defn- page-lineage [page all-pages]
  (if-let [parent (page/retrieve-value page :parent)]
    (conj (page-lineage (get all-pages parent) all-pages) page)
    [page]))

(defn- remove-this-page [pages page]
  (remove #(= page %) pages))

(defn- get-pages [{pages :pages :as page} {all-pages :all-pages}]
  (remove-this-page (map (partial get all-pages) pages) page))

(defn find-modified [page opts]
  (apply max 0 (concat (map #(page/retrieve-value % :modified opts)
                            (get-pages page opts))
                       (map #(page/retrieve-value % :src-modified)
                            (page-lineage page (:all-pages opts))))))

(def transform [:modified find-modified])
