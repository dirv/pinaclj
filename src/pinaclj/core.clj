(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.read :as rd]
            [markdown.core :as markdown]))

(defn- render-markdown [page]
  (assoc page :content (markdown/md-to-html-string (:content page))))

(defn- render [page template]
  (->> page
       render-markdown
       template
       (apply str)))

(def build-destination
  (comp files/change-extension-to-html files/change-root))

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn compile-all [src dest template]
  (doseq [page-path (files/all-in src)]
    (let [page (rd/read-page page-path)]
      (if (published? page)
        (files/create (build-destination src dest page-path)
                      (render page template))))))
