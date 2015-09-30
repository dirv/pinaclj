(ns pinaclj.transforms.prev
  (:require [pinaclj.page :as page]))

(defn- prev-in-list [items item]
  (last (take-while #(not= % item) items)))

(defn prev-url [page opts]
  (prev-in-list (page/retrieve-value page :page-list opts)
                (page/retrieve-value page :destination opts)))

(defn choose-prev [page opts]
  (when-let [prev-url (prev-url page opts)]
    {:attrs {:href prev-url}}))

(def transform [:prev choose-prev])
