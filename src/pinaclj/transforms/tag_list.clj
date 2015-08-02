(ns pinaclj.transforms.tag-list)

(defn get-tags [page opts]
  {:pages (vals (:tag-pages page))})

(def transform [:tag-list get-tags])
