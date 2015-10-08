(ns pinaclj.transforms.published-at
  (:require [pinaclj.date-time :as date-time]))

(def default-format
  "d MMMM yyyy")

(defn- to-format [opts]
  (when (and (contains? opts :format)
           (date-time/valid? (:format opts)))
    (:format opts)))

(defn to-readable-str [page opts]
  (if (contains? page :published-at)
    (if-let [fmt (to-format opts)]
      (date-time/to-readable-str (:published-at page) fmt)
      (:published-at page))))

(def transform [:published-at to-readable-str])
