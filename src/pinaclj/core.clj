(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(def index-page
  "index.html")

(defn- dest-last-modified-fn [dest]
  (let [index-file (nio/resolve-path dest index-page)]
    (if (nio/exists? index-file)
      (nio/get-last-modified-time index-file)
      0)))

(def dest-last-modified
  (memoize dest-last-modified-fn))

(defn- modified-since-last-publish? [dest page]
  (> (page/retrieve-value page :modified {})
     (dest-last-modified dest)))

(defn- dest-path [dest page]
  (nio/resolve-path dest (page/retrieve-value page :destination {})))

(defn- templated-content [page template]
  (page/retrieve-value page :templated-content {:template template}))

(defn- write-page [dest template page]
  (files/create (dest-path dest page)
                (templated-content page template)))

(defn- create-pages [src]
  (map (partial pb/create-page src) (files/all-in src)))

(defn- modified-pages [dest pages]
  (filter #(modified-since-last-publish? dest %) pages))

(defn- write-pages [dest theme pages]
  (doall (map #(write-page dest
                           (theme/get-template theme (first %))
                           (second %))
              pages)))

(defn- generate-list [modified-pages pages theme]
  (let [tags (pb/build-tag-pages pages)
        attached-pages (pb/attach-tag-pages modified-pages tags)]
  (concat (map #(vector :post %) attached-pages)
          (map #(vector :index.html %) (vals tags))
          (map #(vector % (pb/build-list-page pages (name %))) (theme/root-pages theme)))))

(defn- divide-page [theme [template page]]
  (map #(vector template %)
       (pb/divide page (theme/get-template theme template))))

(defn- divide-pages [pages theme]
  (mapcat #(divide-page theme %) pages))

(defn compile-all [fs src-path dest-path theme-path]
  (let [pages (pb/load-published-pages (create-pages (nio/resolve-path fs src-path)))
        theme (theme/build-theme fs theme-path)
        dest (nio/resolve-path fs dest-path)
        modified-pages (pb/load-published-pages (modified-pages dest pages))]
    (write-pages dest theme (divide-pages (generate-list modified-pages pages theme) theme))))
