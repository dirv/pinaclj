(ns pinaclj.transforms.page-list
  (:require [pinaclj.page :as page]))

(defn- max-pages [{max-pages :max-pages}]
  (when-not (nil? max-pages)
    (Integer/parseInt max-pages)))

(defn- apply-max-pages [pages opts]
  (if-let [max-pages (max-pages opts)]
    (subvec (vec pages) 0 max-pages)
    pages))

(defn clone-pages [page-set opts]
  {:pages (apply-max-pages (get page-set :pages []) opts)})

(def transform [:page-list clone-pages])
