(ns pinaclj.transforms.total-child-pages
  (:require [pinaclj.page :as page]))

(defn calculate-total-child-pages [page opts]
  (count (:pages (page/retrieve-value page :page-list opts))))

(def transform [:total-child-pages calculate-total-child-pages])
