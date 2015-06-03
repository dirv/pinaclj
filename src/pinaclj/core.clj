(ns pinaclj.core
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.read :as rd]
            [endophile.core :as md]
            [pinaclj.link-transform :as link]
            [pinaclj.punctuation-transform :as punctuation]
            [pinaclj.templates :as templates]
            [pinaclj.page :as page]))

(def index-page
  "index.html")

(def feed-page
  "feed.xml")

(def render-markdown
  (comp md/to-clj md/mp))

(defn- apply-transforms [page template]
  (-> (assoc page :content (template page))
      punctuation/transform
      link/transform
      :content))

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

(defn- add-rendered-content [page]
  (page/set-lazy-value page
                       :content
                       (fn [page opts]
                         (render-markdown (:raw-content page)))))

(defn- compile-page [src page-path]
  (let [page (rd/read-page page-path)]
    (when (published? page)
      (add-rendered-content
        (assoc page
             :url (publication-path page src page-path))))))

(defn- compile-pages [src files]
  (remove nil? (map (partial compile-page src) files)))

(defn- write-single-page [dest page template]
  (files/create (nio/resolve-path dest (:url page))
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
