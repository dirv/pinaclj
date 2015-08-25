(ns pinaclj.page
  (:require [pinaclj.nio :as nio]
            [pinaclj.files :as files]))

(defn set-lazy-value [page fk fv]
  (assoc-in page [:funcs fk] (memoize fv)))

(defn- contains-in? [page ks]
  (get-in page ks))

(defn- has-link-to? [page opts k]
  (and (contains? opts :all-pages) (contains? page k)))

(defn- linked-page [page opts k]
  (get (:all-pages opts) (name (get page k))))

(defn retrieve-value [page k opts]
  (cond
    (contains-in? page [:funcs k])
    ((get-in page [:funcs k]) page opts)
    (contains? page k)
    (get page k)
    (has-link-to? page opts :parent)
    (retrieve-value (linked-page page opts :parent) k opts)
    (has-link-to? page opts :category)
    (retrieve-value (linked-page page opts :category) k opts)))

(defn print-page [page]
  (print "{")
  (doall (map #(println % " " (retrieve-value page % {}))
              (keys page)))
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

(defn- chronological-sort [pages]
  (reverse (sort-by #(:published-at (val %)) pages)))

(defn- without-generated [pages]
  (filter #(not (:generated (val %))) pages))

(defn children [page-set all-pages]
  (or (:pages page-set)
      (keys (chronological-sort (without-generated all-pages)))))
