(ns pinaclj.page)

(defn set-lazy-value [page fk fv]
  (assoc-in page [:funcs fk] (memoize fv)))

(defn retrieve-value [page k opts]
  (if (contains? page k)
    (get page k)
    ((get (:funcs page) k) page opts)))
