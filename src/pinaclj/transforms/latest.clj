(ns pinaclj.transforms.latest)

(defn get-latest [{pages :pages} opts]
  {:page (last (sort-by :published-at pages))})

(def transform [:latest get-latest])
