(ns pinaclj.transforms.latest)

(defn get-latest [{pages :pages} opts]
  {:page (last (sort-by :published-at (map #(get (:all-pages opts) %) pages)))})

(def transform [:latest get-latest])
