(ns pinaclj.transforms.url
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]))

(def build-destination
  (comp files/change-extension-to-html nio/relativize))

(defn add-url [page opts]
  (if (contains? page :url)
    (:url page)
    (str "/" (build-destination (:src-root page) (:path page)))))

(def transform [:url add-url])
