(ns pinaclj.transforms.category-link
  (:require [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]))

(defn- create-link [category]
  {:attrs {:href (page/category-url category)}
   :content (name category)})

(defn get-category [page opts]
  (create-link (page/retrieve-value page :category)))

(def transform [:category-link get-category])
