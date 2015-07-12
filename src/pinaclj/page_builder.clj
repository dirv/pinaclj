(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.tag-page :as tp]))

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn build-if-published [page]
  (when (published? page)
      (transforms/apply-all page)))

(defn- create-list-page [pages]
  {:pages pages
   :raw-content ""
   :modified (System/currentTimeMillis)
   :published-at (dt/make 2015 01 01 01 01 01) })

(defn build-list-page [pages]
  (transforms/apply-all (create-list-page pages)))

(defn- build-tag-page [[tag pages]]
  (assoc (build-list-page pages)
         :url
         (str "/tags/" (name tag) "/")))

(defn build-tag-pages [pages]
  (map build-tag-page (tp/pages-by-tag pages)))
