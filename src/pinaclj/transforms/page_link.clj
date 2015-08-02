(ns pinaclj.transforms.page-link
  (:require [pinaclj.page :as page]))

(defn- set-page-link [page opts]
  {:attrs {:href (page/retrieve-value page :destination {})}
   :content (:title page)})

(def transform [:page-link set-page-link])
