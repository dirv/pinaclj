(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]))

(def index-page
  "index.html")

(def feed-page
  "feed.xml")

(defn- compile-page [src page-path]
  (let [page (rd/read-page src page-path)]
    (pb/build-if-published page)))

(defn- compile-pages [src files]
  (remove nil? (map (partial compile-page src) files)))

(defn- dest-last-modified [dest]
  (let [index-file (nio/resolve-path dest index-page)]
    (if (nio/exists? index-file)
      (nio/get-last-modified-time index-file)
      0)))

(defn- modified-since-last-publish? [dest page]
  (> (page/retrieve-value page :modified {})
     ((memoize dest-last-modified) dest)))

(defn- dest-path [dest page]
  (nio/resolve-path dest (page/retrieve-value page :destination {})))

(defn- templated-content [page template]
  (page/retrieve-value page :templated-content {:template template}))

(defn- write-single-page [dest page-func page]
  (when (modified-since-last-publish? dest page)
    (files/create (dest-path dest page)
                  (templated-content page page-func))))

(defn- write-multiple-pages [dest pages page-func]
  (doall (map (partial write-single-page dest page-func) pages)))

(defn compile-all [src dest template-func index-func feed-func]
  (let [pages (compile-pages src (files/all-in src))
        root-page (partial pb/build-list-page pages)]
    (write-multiple-pages dest pages template-func)
    (write-multiple-pages dest (pb/build-tag-pages pages) index-func)
    (write-single-page dest feed-func (root-page feed-page))
    (write-single-page dest index-func (root-page index-page))))
