(ns pinaclj.transforms.page-list)

(defn- max-pages [{max-pages :max-pages}]
  (when-not (nil? max-pages)
    (Integer/parseInt max-pages)))

(defn- chronological-sort [pages]
  (reverse (sort-by #(:published-at (val %)) pages)))

(defn- without-generated [pages]
  (filter #(not (:generated (val %))) pages))

(defn children [page-set all-pages]
  (or (:pages page-set)
      (keys (chronological-sort (without-generated all-pages)))))

(defn clone-pages [page-set opts]
  (let [children (children page-set (:all-pages opts))
        max-pages (max-pages opts)]
    (if (nil? max-pages)
     {:pages children}
     {:pages (take max-pages children)})))

(def transform [:page-list clone-pages])
