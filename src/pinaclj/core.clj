(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(def index-page
  "index.html")

(defn- compile-page [src page-path]
  (let [page (rd/read-page src page-path)]
    (pb/build-if-published page)))

(defn- compile-pages [src files]
  (remove nil? (map (partial compile-page src) files)))

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

(defn- write-page [dest page-func page]
  (when (modified-since-last-publish? dest page)
    (files/create (dest-path dest page)
                  (templated-content page page-func))))

(defn- read-pages [src]
  (compile-pages src (files/all-in src)))

(defn- write-pages [dest theme pages]
  (doall (map #(write-page dest
                           (theme/get-template theme (first %))
                           (second %))
              pages)))

(defn- generate-list [pages theme]
  (concat (map #(vector :post %) pages)
          (map #(vector :index.html %) (pb/build-tag-pages pages))
          (map #(vector % (pb/build-list-page pages (name %))) (theme/root-pages theme))))

(defn compile-all [fs src-path dest-path theme-path]
  (let [pages (read-pages (nio/resolve-path fs src-path))
        theme (theme/build-theme fs theme-path)
        dest (nio/resolve-path fs dest-path)]
    (write-pages dest theme (generate-list pages theme))))
