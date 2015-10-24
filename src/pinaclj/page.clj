(ns pinaclj.page
  (:require [pinaclj.nio :as nio]
            [pinaclj.files :as files]))

(defn category-url [category]
  (str "category/" (name category) "/index.html"))

(defn tag-url [tag]
  (str "tag/" (name tag) "/index.html"))

(defn set-lazy-value [page fk fv]
  (assoc-in page [:funcs fk] (memoize fv)))

(defn- contains-in? [page ks]
  (get-in page ks))

(defn- is-index? [page opts]
  (= page (get (:all-pages opts) "index.html")))

(defn- without-page [opts url]
  (assoc opts
         :all-pages
         (dissoc (:all-pages opts) url)))

(declare retrieve-value)

(defn- retrieve-parent-value [page opts k]
  (when-let [linked-url (retrieve-value page :parent)]
    (when-let [linked-page (get (:all-pages opts) linked-url)]
      (retrieve-value linked-page k (without-page opts linked-url)))))

(defn retrieve-value
  ([page k]
   (retrieve-value page k {}))
  ([page k opts]
   (cond
     (contains-in? page [:funcs k])
     ((get-in page [:funcs k]) page opts)
     (contains? page k)
     (get page k)
     (not= :parent k)
     (retrieve-parent-value page opts k))))

(defn print-page [page]
  (print "{")
  (doall (map #(println % " " (retrieve-value page %))
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

(defn to-page [page-url opts]
  (get (:all-pages opts) page-url))

(defn to-page-urls [pages]
  (map #(retrieve-value % :destination) pages))
