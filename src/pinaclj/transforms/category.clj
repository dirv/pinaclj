(ns pinaclj.transforms.category)

(def default-category 
  :post)

(defn convert-category [page opts]
  (if-let [category (:category page)]
    (keyword category)
    default-category))

(def transform [:category convert-category])
