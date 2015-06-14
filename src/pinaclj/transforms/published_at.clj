(ns pinaclj.transforms.published-at
  (:require [pinaclj.date-time :as date-time]
            [pinaclj.page :as page]))

(def default-format
  "d MMMM yyyy")

(defn- to-format [opts]
  (if (and (contains? opts :format)
           (date-time/valid? (:format opts)))
    (:format opts)
    default-format))

(defn to-readable-str [page opts]
    (date-time/to-readable-str (:published-at page) (to-format opts)))

(defn apply-transform [page]
  (page/set-lazy-value page :published-at to-readable-str))

