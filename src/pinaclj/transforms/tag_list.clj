(ns pinaclj.transforms.tag-list
  (:require [pinaclj.page :as page]))


(defn- build-tag-page [tag]
  {:title tag :url (str "/tags/" tag "/")})

(defn get-tags [page opts]
  {:pages (map build-tag-page (page/retrieve-value page :tags {}))})

(defn apply-transform [page]
  (page/set-lazy-value page :tag-list get-tags))
