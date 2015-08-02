(ns pinaclj.transforms.tags)

(defn get-tags [page opts]
  (if (:tags page)
    (map clojure.string/trim (clojure.string/split (:tags page) #","))
    []))

(def transform [:tags get-tags])
