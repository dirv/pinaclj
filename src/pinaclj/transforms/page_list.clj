(ns pinaclj.transforms.page-list
  (:require [pinaclj.page :as page]
            [pinaclj.children :as children]))

(defn- max-pages [{max-pages :max-pages}]
  (when-not (nil? max-pages)
    (Integer/parseInt max-pages)))

(defn- already-computed? [{max-pages :max-pages}]
  max-pages)

(defn- filter-pages [page-set opts]
  (if (already-computed? opts)
    (:pages page-set)
    (children/children page-set opts (vals (:all-pages opts)))))

(defn- apply-max-pages [pages opts]
  (if-let [max-pages (max-pages opts)]
    (subvec (vec pages) 0 (min max-pages (count pages)))
    pages))

(defn clone-pages [page-set opts]
  {:pages (apply-max-pages (filter-pages page-set opts) opts)})

(def transform [:page-list clone-pages])
