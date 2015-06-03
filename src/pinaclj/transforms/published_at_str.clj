(ns pinaclj.transforms.published-at-str
  (:require [pinaclj.date-time :as date-time]
            [pinaclj.page :as page]))

(defn- to-readable-str [page opts]
  (date-time/to-readable-str (:published-at page)))

(defn apply-transform [page]
  (page/set-lazy-value page :published-at-str to-readable-str))

