(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [pinaclj.link-transform :as link]
            [pinaclj.punctuation-transform :as punctuation]
            [pinaclj.templates :as templates]
            [pinaclj.page :as page]
            [pinaclj.transforms.transforms :as transforms]))

(def index-page
  "index.html")

(def feed-page
  "feed.xml")

(defn- apply-transforms [page template]
  (-> (assoc page :content (template page))
      punctuation/transform
      link/transform
      :content))

(defn- published? [page]
  (not (nil? (:published-at page))))

(defn- compile-page [src page-path]
  (let [page (rd/read-page page-path)]
    (when (published? page)
      (-> page
          (assoc :src src) ; todo, possibly move to read-page
          (transforms/apply-all)))))

(defn- compile-pages [src files]
  (remove nil? (map (partial compile-page src) files)))

(defn- write-single-page [dest page template]
  (files/create (nio/resolve-path dest (page/retrieve-value page :destination {}))
                (templates/to-str (apply-transforms page template))))

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
