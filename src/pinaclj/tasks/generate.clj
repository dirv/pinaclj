(ns pinaclj.tasks.generate
  (:require [pinaclj.files :as files]
            [pinaclj.read :as rd]
            [pinaclj.nio :as nio]
            [pinaclj.site :as site]
            [pinaclj.tasks.task :as task]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]
            [taoensso.tower :as tower]
            [pinaclj.translate :refer :all]))

(def index-page
  "index.html")

(defn- dest-last-modified [dest]
  (let [index-file (nio/resolve-path dest index-page)]
    (if (nio/exists? index-file)
      (nio/get-last-modified-time index-file)
      0)))

(defn- write-page [dest-root [page-path content]]
  (files/create (nio/resolve-path dest-root page-path) content)
  (task/success (t :en :published-page page-path)))

(defn- create-page [src-root src-file]
  (rd/read-page (pb/create-page src-root src-file)))

(defn- create-pages [src]
  (map #(create-page src %) (files/all-in src)))

(defn- copy-file [src dest-root page-path]
  (let [new-path (nio/resolve-path dest-root (nio/relativize src page-path))]
    (nio/copy-file page-path new-path)))

(defn- write-static-files [theme src dest]
  (map #(copy-file src dest %) (:files theme)))

(defn generate [fs src-path-str dest-path-str theme-path-str]
  (tower/with-tscope :generate
    (let [src (nio/resolve-path fs src-path-str)
          dest (nio/resolve-path fs dest-path-str)
          theme-path (nio/resolve-path fs theme-path-str)
          theme (theme/build-theme fs theme-path)
          pages (create-pages src)]
      (doall
        (concat (map (partial write-page dest) (site/build pages theme (dest-last-modified dest)))
                (write-static-files theme theme-path dest))))))
