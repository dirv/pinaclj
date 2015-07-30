(ns pinaclj.transforms.tag-list
  (:require [pinaclj.page :as page]))

(defn get-tags [page opts]
  {:pages (vals (:tag-pages page))})

(defn apply-transform [page]
  (page/set-lazy-value page :tag-list get-tags))
