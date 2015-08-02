(ns pinaclj.transforms.published-at
  (:require [pinaclj.date-time :as date-time]))

(def default-format
  "d MMMM yyyy")

(defn- to-format [opts]
  (if (and (contains? opts :format)
           (date-time/valid? (:format opts)))
    (:format opts)
    default-format))

(defn to-readable-str [page opts]
  (if (contains? page :published-at)
    (date-time/to-readable-str (:published-at page) (to-format opts))
    ""))

(def transform [:published-at to-readable-str])
