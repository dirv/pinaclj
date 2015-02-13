(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [markdown.core :as markdown]
            [pinaclj.quote-transform :as quotes]
            [pinaclj.templates :as templates]))

(def index-page
  "index.html")

(defn- render-markdown [page]
  (assoc page :content (quotes/convert-quote-text (markdown/md-to-html-string (:content page)))))

(def build-destination
  (comp files/change-extension-to-html nio/relativize))

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

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn- publication-path [page src page-path]
  (if (nil? (:url page))
    (build-destination src page-path)
    (fix-url (:url page))))

(defn- compile-page [src page-path]
  (let [page (rd/read-page page-path)]
    (if (published? page)
        (render-markdown
          (assoc page :url (publication-path page src page-path))))))

(defn- compile-pages [src files]
  (remove nil? (map (partial compile-page src) files)))

(defn- write-templated-page [dest path content template]
   (files/create (nio/resolve-path dest path)
                 (apply str (template content))))

(defn- chronological-sort [pages]
  (reverse (sort-by :published-at pages)))

(defn compile-all [src dest template-func index-func]
  (let [pages (compile-pages src (files/all-in src))]
    (doall (map #(write-templated-page dest (:url %) % template-func) pages))
    (write-templated-page dest
                          index-page
                          (chronological-sort pages)
                          index-func)))
