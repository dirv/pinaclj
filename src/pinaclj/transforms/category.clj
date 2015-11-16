(ns pinaclj.transforms.category)

(def default-category
  :post)

(defn convert-category [page opts]
  (-> (get page :category default-category)
      keyword))

(def transform [:category convert-category])
