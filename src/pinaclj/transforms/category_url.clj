(ns pinaclj.transforms.category-url
  (:require [pinaclj.group :as group]
            [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]))

(defn get-category [page opts]
  (when-let [category (page/retrieve-value page :category {})]
    (group/category-url category)))

(def transform [:category-url get-category])
