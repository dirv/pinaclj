(ns pinaclj.transforms.category-link
  (:require [pinaclj.group :as group]
            [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]))

(defn- create-page [category]
  (transforms/apply-all {:title (name category)
                         :destination (group/category-url category)}))

(defn get-category [page opts]
  {:page (create-page (page/retrieve-value page :category {}))})

(def transform [:category-link get-category])
