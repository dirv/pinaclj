(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [pinaclj.templates :as templates]
            [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]))

(def index-page
  "index.html")

(def feed-page
  "feed.xml")

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn- compile-page [src page-path]
  (let [page (rd/read-page src page-path)]
    (when (published? page)
      (transforms/apply-all page))))

(defn- compile-pages [src files]
  (remove nil? (map (partial compile-page src) files)))

(defn- dest-last-modified [dest]
  (let [index-file (nio/resolve-path dest index-page)]
    (if (nio/exists? index-file)
      (nio/get-last-modified-time index-file)
      0)))

(defn- modified-since-last-publish? [dest page]
  (> (:modified page) (dest-last-modified dest)))

(defn- dest-path [dest page]
  (nio/resolve-path dest (page/retrieve-value page :destination {})))

(defn- templated-content [page template]
  (page/retrieve-value page :templated-content {:template template}))

(defn- write-single-page [dest page template]
  (when (modified-since-last-publish? dest page)
    (files/create (dest-path dest page)
                  (templated-content page template))))

(defn- write-list-page [dest path pages template]
  (files/create (nio/resolve-path dest path)
                (templates/to-str (template pages))))

(defn- chronological-sort [pages]
  (reverse (sort-by :published-at pages)))

(defn compile-all [src dest template-func index-func feed-func]
  (let [pages (compile-pages src (files/all-in src))]
    (doall (map #(write-single-page dest % template-func) pages))
    (write-list-page dest
                     feed-page
                     (chronological-sort pages)
                     feed-func)
    (write-list-page dest
                     index-page
                     (chronological-sort pages)
                     index-func)))
