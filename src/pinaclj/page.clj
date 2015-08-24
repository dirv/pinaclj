(ns pinaclj.page
  (:require [pinaclj.nio :as nio]
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

(def non-written-headers #{:read-headers :raw-content :path :funcs :src-root})

(defn- header-keys [page]
  (clojure.set/difference (set (keys page)) non-written-headers))

(defn- ordered-header-keys [page]
  (concat (:read-headers page)
          (clojure.set/difference (header-keys page)
                                  (set (:read-headers page)))))

(defn- to-headers [page]
  (apply str (map #(str (name %) ": " (% page) "\n")
                  (ordered-header-keys page))))

(defn write-page [page fs]
  (files/create (nio/resolve-path fs (:path page))
                (str (to-headers page) "---\n"  (:raw-content page) "\n")))

(defn to-page-urls [pages]
  (map #(retrieve-value % :destination {}) pages))
