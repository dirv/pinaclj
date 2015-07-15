(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.read :as rd]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.tag-page :as tp]))

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn load-published-pages [pages]
  (filter published? (map rd/read-page pages)))

(defn create-page [src path]
  (transforms/apply-all {:path path :src-root src}))

(defn- create-list-page [pages url]
  {:pages pages
   :raw-content ""
   :url url
   :modified (System/currentTimeMillis)
   :published-at (dt/make 2015 01 01 01 01 01) })

(defn build-list-page [pages url]
  (transforms/apply-all (create-list-page pages url)))

(defn- build-tag-page [[tag pages]]
  (build-list-page pages (str "/tags/" (name tag) "/")))

(defn build-tag-pages [pages]
  (map build-tag-page (tp/pages-by-tag pages)))
