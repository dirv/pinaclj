(ns pinaclj.page
  (:require [pinaclj.nio :as nio]
            [pinaclj.files :as files]))

(defn set-lazy-value [page fk fv]
  (assoc-in page [:funcs fk] (memoize fv)))

(defn- contains-in? [page ks]
  (get-in page ks))

(defn- is-index? [page opts]
  (= page (get (:all-pages opts) "index.html")))

(declare retrieve-value)

(defn- linked-page [page opts k]
  (when-let [url (retrieve-value page k {})]
    (get (:all-pages opts) url)))

(defn retrieve-value [page k opts]
  (cond
    (contains-in? page [:funcs k])
    ((get-in page [:funcs k]) page opts)
    (contains? page k)
    (get page k)
    (contains? page :parent)
    (retrieve-value (linked-page page opts :parent) k opts)
    (and (contains? opts :all-pages)
         (not (is-index? page opts))
         (contains? page :category))
    (retrieve-value (linked-page page opts :category-url) k opts)))

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

(defn- except-page [page all-pages]
  (dissoc all-pages (retrieve-value page :destination {})))

(def without-generated
  (partial remove #(:generated (val %))))

(defn- all-page-urls [all-pages]
  (map key all-pages))

(defn children [page-set all-pages]
  (or (:pages page-set)
      (-> page-set
          (except-page all-pages)
          (without-generated)
          (chronological-sort)
          (all-page-urls))))
