(ns pinaclj.transforms.url
  (:require [pinaclj.page :as page]
            [pinaclj.files :as files]
            [pinaclj.nio :as nio]))

(def build-destination
  (comp files/change-extension-to-html nio/relativize))

(defn add-url [page opts]
  (if (contains? page :url)
    (:url page)
    (str "/" (build-destination (:src-root page) (:path page)))))

(defn apply-transform [page]
  (page/set-lazy-value page :url add-url))
