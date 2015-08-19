(ns pinaclj.transforms.tag-list
  (:require [pinaclj.group :as group]))

(defn- to-link-page [tag]
  {:title (name tag)
   :destination (group/tag-url tag)})

(defn get-tags [page opts]
  {:pages (map to-link-page (:tags page))})

(def transform [:tag-list get-tags])
