(ns pinaclj.transforms.category)

(defn- convert-category [page opts]
  (when-let [category (:category page)]
    (keyword category)))

(def transform [:category convert-category])
