(ns pinaclj.transforms.category-link
  (:require [pinaclj.group :as group]
            [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]))

(defn- create-link [category]
  {:attrs {:href (group/category-url category)}
   :content (name category)})

(defn get-category [page opts]
  (create-link (page/retrieve-value page :category {})))

(def transform [:category-link get-category])
