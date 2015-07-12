(ns pinaclj.transforms.modified
  (:require [pinaclj.page :as page]))

(defn- find-modified [page opts]
  (if (contains? page :pages)
    (apply max (map :modified (:pages page)))
    (:modified page)))

(defn apply-transform [page]
  (page/set-lazy-value page :modified find-modified))
