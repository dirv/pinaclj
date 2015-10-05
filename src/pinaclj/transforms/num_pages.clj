(ns pinaclj.transforms.num-pages)

(defn calculate-num-pages [page opts]
  (count (:pages page)))

(def transform [:num-pages calculate-num-pages])
