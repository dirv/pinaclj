(ns pinaclj.transforms.tags
  (:require [pinaclj.page :as page]))

(defn get-tags [page opts]
  (when (:tags page)
    (map clojure.string/trim (clojure.string/split (:tags page) #","))))

(defn apply-transform [page]
  (page/set-lazy-value page :tags get-tags))
