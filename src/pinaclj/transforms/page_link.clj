(ns pinaclj.transforms.page-link
  (:require [pinaclj.page :as page]))

(defn- find-page [page {all-pages :all-pages page-key :key}]
  (let [title (page/retrieve-value page (keyword page-key) {})]
    (val (first (filter #(= title (:title (val %))) all-pages)))))

(defn- determine-page [page opts]
  (if (contains? opts :key)
    (find-page page opts)
    page))

(defn- build-page-link [page]
  {:attrs {:href (page/retrieve-value page :destination {})}
   :content (:title page)})

(defn set-page-link [page opts]
  (build-page-link (determine-page page opts)))

(def transform [:page-link set-page-link])
