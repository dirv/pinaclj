(ns pinaclj.transforms.page-link
  (:require [pinaclj.page :as page]))

(defn- set-page-link [page opts]
  {:attrs {:href (page/retrieve-value page :destination {})}
   :content (:title page)})

(defn apply-transform [page]
  (page/set-lazy-value page :page-link set-page-link))
