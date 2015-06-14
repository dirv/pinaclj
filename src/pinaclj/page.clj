(ns pinaclj.page)

(defn set-lazy-value [page fk fv]
  (assoc-in page [:funcs fk] (memoize fv)))

(defn retrieve-value [page k opts]
  (if (and (contains? page :funcs)
           (contains? (:funcs page) k))
    ((get (:funcs page) k) page opts)
    (get page k)))

(defn all-keys [page]
  (concat (remove #(= :funcs %) (keys page)) (keys (:funcs page))))
