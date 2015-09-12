(ns pinaclj.transforms.modified
  (:require [pinaclj.page :as page]
            [pinaclj.nio :as nio]))

(defn- get-last-modified-time [page]
  (nio/get-last-modified-time (:path page)))

(defn- get-pages [{pages :pages} {all-pages :all-pages}]
  (map (partial get all-pages) pages))

(defn find-modified [page opts]
  (if (contains? page :pages)
    (apply max (map #(page/retrieve-value % :modified {})
                    (get-pages page opts)))
    (get-last-modified-time page)))

(def transform [:modified find-modified])
