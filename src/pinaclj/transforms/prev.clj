(ns pinaclj.transforms.prev
  (:require [pinaclj.page :as page]))

(defn- to-page [url {all-pages :all-pages}]
  (get all-pages url))

(defn- prev-in-list [items item]
  (last (take-while #(not= % item) items)))

(defn choose-prev [page opts]
  (to-page (prev-in-list (page/retrieve-value page :page-list opts)
                         (page/retrieve-value page :destination opts)) opts))

(def transform [:prev choose-prev])
