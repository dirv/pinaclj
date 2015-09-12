(ns pinaclj.transforms.url
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]))

(defn- relativized-path [{src-root :src-root path :path}]
  (if (nil? src-root)
    path
    (nio/relativize src-root path)))

(def build-destination
  (comp files/change-extension-to-html relativized-path))

(defn add-url [page opts]
  (or (:url page)
      (str "/" (build-destination page))))

(def transform [:url add-url])
