(ns pinaclj.transforms.category-link
  (:require [pinaclj.group :as group]
            [pinaclj.transforms.transforms :as transforms]))

(defn- create-page [category]
  (transforms/apply-all {:title (name category)
                         :destination (group/category-url category)}))

(defn get-category [{category :category} opts]
  {:page (create-page category)})

(def transform [:category-link get-category])
