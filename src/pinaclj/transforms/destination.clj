(ns pinaclj.transforms.destination
  (:require [pinaclj.page :as page]
            [pinaclj.files :as f]))

(def index-page
  "index.html")

(defn- add-index-page-extension [url-str]
  (if (= \/ (last url-str))
    (str url-str index-page)
    url-str))

(def fix-url
  (comp f/trim-url add-index-page-extension))

(defn add-destination [page opts]
  (fix-url (page/retrieve-value page :url {})))

(def transform [:destination add-destination])
