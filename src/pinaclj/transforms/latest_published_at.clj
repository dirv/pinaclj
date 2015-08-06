(ns pinaclj.transforms.latest-published-at
  (:require [pinaclj.date-time :as date]))

(defn- find-latest-page [page-set opts]
  (.toString (last (sort-by :published-at (map :published-at (:pages page-set))))))

(def transform [:latest-published-at find-latest-page])
