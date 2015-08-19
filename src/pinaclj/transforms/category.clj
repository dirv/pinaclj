(ns pinaclj.transforms.category)

(def uncategorized
  :uncategorized)

(defn convert-category [page opts]
  (if-let [category (:category page)]
    (keyword category)
    uncategorized))

(def transform [:category convert-category])
