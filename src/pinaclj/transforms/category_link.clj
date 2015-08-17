(ns pinaclj.transforms.category-link
  )

(defn get-category [{category :category} opts]
  {:page {:title (name category)
          :destination (str "category/" (name category) "/")}})

(def transform [:category-link get-category])
