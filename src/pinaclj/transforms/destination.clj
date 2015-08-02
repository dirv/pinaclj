(ns pinaclj.transforms.destination
  (:require [pinaclj.page :as page]))

(def index-page
  "index.html")

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

(defn add-destination [page opts]
  (fix-url (page/retrieve-value page :url {})))

(def transform [:destination add-destination])
