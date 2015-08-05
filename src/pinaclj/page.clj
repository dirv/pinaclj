(ns pinaclj.page
  (:require [pinaclj.date-time :as date]
            [pinaclj.nio :as nio]
            [pinaclj.files :as files]))

(defn set-lazy-value [page fk fv]
  (assoc-in page [:funcs fk] (memoize fv)))

(defn retrieve-value [page k opts]
  (if (and (contains? page :funcs)
           (contains? (:funcs page) k))
    ((get (:funcs page) k) page opts)
    (get page k)))

(defn all-keys [page]
  (distinct (concat (remove #(= :funcs %) (keys page)) (keys (:funcs page)))))

(defn print-page [page]
  (print "{")
  (doall (map #(println % " " (retrieve-value page % {}))
              (all-keys page)))
  (println "}"))

(def non-written-headers #{:raw-content :path :funcs :src-root})

(defn- written-kv-headers [page]
  (filter #(not (non-written-headers (key %))) page))

(defn- to-headers [page]
  (apply str (map #(str (name (key %)) ": " (val %) "\n")
                  (written-kv-headers page))))

(defn write-page [page fs]
    (files/create (nio/resolve-path fs (:path page))
                  (str (to-headers page) "---\n"  (:raw-content page))))
