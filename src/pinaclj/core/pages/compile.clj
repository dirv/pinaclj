(ns pinaclj.core.pages.compile
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.pages.read :as rd]
            [markdown.core :as markdown]))

(defn- compiled-filename [page-path]
  (let [filename (str (last page-path))
        no-ext   (subs filename 0 (.lastIndexOf filename "."))]
    (str no-ext ".html")))

(defn- destination [destination-dir page-path]
  (str destination-dir "/" (compiled-filename page-path)))

(defn- render-markdown [page]
  (assoc page :content (markdown/md-to-html-string (:content page))))

(defn- render [page-path template]
  (->> page-path
       rd/read-page
       render-markdown
       template
       (apply str)))

(defn- write-rendered [destination-dir page-path template]
  (files/create (destination destination-dir page-path)
                (render page-path template)))

(defn run [source-dir destination-dir template]
  (doseq [page (files/all-in source-dir)]
    (write-rendered destination-dir page template)))
