(ns pinaclj.transforms.parent
  (:require [pinaclj.page :as page]))

(defn- choose-parent [page opts]
  (cond
    (= "index.html" (page/retrieve-value page :destination {}))
    nil
    (contains? page :parent)
    (:parent page)
    :else
    (if-let [category (page/retrieve-value page :category {})]
      (page/category-url category))))

(def transform [:parent choose-parent])
