(ns pinaclj.transforms.next
  (:require [pinaclj.page :as page]))

(defn- to-page [url {all-pages :all-pages}]
  (get all-pages url))

(defn- next-in-list [items item]
  (second (drop-while #(not= % item) items)))

(defn choose-next [page opts]
  (to-page (next-in-list (page/retrieve-value page :page-list opts)
                         (page/retrieve-value page :destination opts)) opts))

(def transform [:next choose-next])
