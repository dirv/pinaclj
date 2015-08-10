(ns pinaclj.tasks.generate
  (:require [pinaclj.files :as files]
            [pinaclj.read :as rd]
            [pinaclj.nio :as nio]
            [pinaclj.site :as site]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(def index-page
  "index.html")

(defn- dest-last-modified [dest]
  (let [index-file (nio/resolve-path dest index-page)]
    (if (nio/exists? index-file)
      (nio/get-last-modified-time index-file)
      0)))

(defn- write-page [dest-root [page-path content]]
  (files/create (nio/resolve-path dest-root page-path) content))

(defn- create-page [src-root src-file]
  (rd/read-page (pb/create-page src-root src-file)))

(defn- create-pages [src]
  (map #(create-page src %) (files/all-in src)))

(defn generate [fs src-path dest-path theme-path]
  (let [pages (create-pages (nio/resolve-path fs src-path))
        theme (theme/build-theme fs theme-path)
        dest (nio/resolve-path fs dest-path)]
    (doall (map (partial write-page dest) (site/build pages theme (dest-last-modified dest))))))
