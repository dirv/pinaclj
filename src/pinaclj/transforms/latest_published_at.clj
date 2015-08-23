(ns pinaclj.transforms.latest-published-at
  (:require [pinaclj.date-time :as date]))

(defn- to-pages [page-urls all-pages]
  (map #(get all-pages %) page-urls))

(defn- find-latest-page [page-set {all-pages :all-pages}]
  (when (some? (:pages page-set))
    (.toString (last (sort-by :published-at (map :published-at (to-pages (:pages page-set) all-pages)))))))

(def transform [:latest-published-at find-latest-page])
