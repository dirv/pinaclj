(ns pinaclj.core.pages.compile
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.pages.read :as rd]
            [markdown.core :as markdown]))

(defn- compiled-filename [page-path]
  (let [filename (str (last page-path))
        no-ext   (subs filename 0 (.lastIndexOf filename "."))]
    (str no-ext ".html")))

(defn- destination [to-dir page-path]
  (str to-dir "/" (compiled-filename page-path)))

(defn- render-markdown [page]
  (assoc page :content (markdown/md-to-html-string (:content page))))

(defn- render [page-path template]
  (->> page-path
       rd/read-page
       render-markdown
       template
       (apply str)))

(defn- write-rendered [to-dir page-path template]
  (files/create (destination to-dir page-path) (render page-path template)))

(defn run [from-dir to-dir template]
  (doseq [page (files/all-in from-dir)]
    (write-rendered to-dir page template)))
