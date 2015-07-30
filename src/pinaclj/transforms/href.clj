(ns pinaclj.transforms.href
  (:require [pinaclj.page :as page]))

(defn- set-href [page opts]
  {:attrs {:href (:url page)}})

(defn apply-transform [page]
  (page/set-lazy-value page :href set-href))
