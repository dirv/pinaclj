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
  (files/timestamp (nio/resolve-path dest index-page)))

(defn- write-page [dest-root [page-path content]]
  (files/create (nio/resolve-path dest-root page-path) content)
  (task/success (t :en :published-page page-path)))

(defn- create-page [src-root src-file]
  (rd/read-page (pb/create-page src-root src-file)))

(defn- create-pages [src]
  (map #(create-page src %) (files/all-in src)))

(defn- relative-file-name [src-root src-file]
  (.toString (nio/relativize src-root src-file)))

(defn- write-static-file [src-root dest-root src-file]
  (if (files/duplicate-if-newer src-root dest-root src-file)
    (task/success (t :en :copy-file (relative-file-name src-root src-file)))
    (task/info (t :en :did-not-copy-file (relative-file-name src-root src-file)))))

(defn- write-static-files [theme src dest]
  (map (partial write-static-file src dest) (:static-files theme)))

(defn- write-generated-files [theme src dest]
  (map (partial write-page dest) (site/build (create-pages src) theme (dest-last-modified dest))))

(defn generate [fs src-path-str dest-path-str theme-path-str]
  (tower/with-tscope :generate
    (let [src (nio/resolve-path fs src-path-str)
          dest (nio/resolve-path fs dest-path-str)
          theme-path (nio/resolve-path fs theme-path-str)
          theme (theme/build-theme fs theme-path)]
      (doall
        (concat (write-generated-files theme src dest)
                (write-static-files theme theme-path dest))))))
