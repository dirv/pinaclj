(ns pinaclj.transforms.href)

(defn- set-href [page opts]
  {:attrs {:href (:url page)}})

(def transform [:href set-href])
