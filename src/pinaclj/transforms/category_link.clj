(ns pinaclj.transforms.category-link
  (:require [pinaclj.group :as group]))

(defn get-category [{category :category} opts]
  {:page {:title (name category)
          :destination (group/category-url category)}})

(def transform [:category-link get-category])
