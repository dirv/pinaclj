(ns pinaclj.transforms.href
  (:require [pinaclj.page :as page]))

(defn- href-container [opts]
  (keyword (:key opts)))

(defn set-href [page opts]
  {:attrs {:href (page/retrieve-value page (href-container opts))}})

(def transform [:href set-href])
