(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [markdown.core :as markdown]))

(def index-page
  "index.html")

(defn- render-markdown [page]
  (assoc page :content (markdown/md-to-html-string (:content page))))

(defn- render [page template]
  (apply str (template (render-markdown page))))

(def build-destination
  (comp files/change-extension-to-html files/change-root))

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn- trim-url [url-str]
  (if (= \/ (first url-str))
    (subs url-str 1)
    url-str))

(defn- add-index-page-extension [url-str]
  (if (= \/ (last url-str))
    (str url-str index-page)
    url-str))

(def fix-url
  (comp trim-url add-index-page-extension))

(defn- publication-path [page src dest page-path]
  (if (nil? (:url page))
    (build-destination src dest page-path)
    (nio/resolve-path dest (fix-url (:url page)))))

(defn compile-all [src dest template]
  (doseq [page-path (files/all-in src)]
    (let [page (rd/read-page page-path)]
      (if (published? page)
        (files/create (publication-path page src dest page-path)
                      (render page template))))))
