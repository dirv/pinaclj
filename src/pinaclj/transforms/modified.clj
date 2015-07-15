(ns pinaclj.transforms.modified
  (:require [pinaclj.page :as page]
            [pinaclj.nio :as nio]))

(defn- get-last-modified-time [page]
  (nio/get-last-modified-time (:path page)))

(defn- find-modified [page opts]
  (if (contains? page :pages)
    (apply max (map #(page/retrieve-value % :modified {})
                    (:pages page)))
    (get-last-modified-time page)))

(defn apply-transform [page]
  (page/set-lazy-value page :modified find-modified))
