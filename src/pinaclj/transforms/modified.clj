(ns pinaclj.transforms.modified
  (:require [pinaclj.page :as page]))

(defn- page-lineage [page all-pages]
  (if-let [parent (page/retrieve-value page :parent {})]
    (conj (page-lineage (get all-pages parent) all-pages) page)
    [page]))

(defn- get-pages [{pages :pages} {all-pages :all-pages}]
  (map (partial get all-pages) pages))

(defn find-modified [page opts]
  (if (contains? page :pages)
    (apply max 0 (map #(page/retrieve-value % :modified opts)
                      (get-pages page opts)))
    (apply max 0 (map #(page/retrieve-value % :src-modified {})
                      (page-lineage page (:all-pages opts))))))

(def transform [:modified find-modified])
