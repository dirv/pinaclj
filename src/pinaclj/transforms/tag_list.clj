(ns pinaclj.transforms.tag-list
  (:require [pinaclj.tag-page :as tp]))

(defn- to-link-page [tag]
  {:title (name tag)
   :destination (tp/tag-url tag)})

(defn get-tags [page opts]
  {:pages (map to-link-page (:tags page))})

(def transform [:tag-list get-tags])
