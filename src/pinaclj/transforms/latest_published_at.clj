(ns pinaclj.transforms.latest-published-at
  (:require [pinaclj.page :as page]
            [pinaclj.date-time :as date]))

(defn- find-latest-page [page-set opts]
  (date/to-str (last (sort-by :published-at (map :published-at (:pages page-set))))))

(defn apply-transform [page-set]
  (page/set-lazy-value page-set :latest-published-at find-latest-page))
