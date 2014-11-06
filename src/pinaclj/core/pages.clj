(ns pinaclj.core.pages
  (:require [pinaclj.core.nio :as nio]
            [pinaclj.core.templates :as templates])
  (:import (java.time Instant Month ZoneId)
           (java.time.format DateTimeFormatter)))

(def header-separator
  "")

(defn- string-to-date [value]
  (when-not (nil? value)
    (.atZone (Instant/parse value) (ZoneId/of "UTC"))))

(defn- to-header [line]
  (let [headed-line (clojure.string/split line #": ")]
    { (keyword (first headed-line)) (second headed-line)}))

(defn- split-header-content [all-lines]
  (let [split (split-with #(not (= header-separator %)) all-lines)]
    [(first split) (rest (second split))]))

(defn- to-headers [header-section]
  (apply merge (map to-header header-section)))

(defn to-page [path fs-root]
  (let [header-and-content (split-header-content (nio/read-all-lines path))
        headers (to-headers (first header-and-content))]
    {:path (nio/get-path-string fs-root path)
     :content (second header-and-content)
     :title (:Title headers)
     :published-at (string-to-date (:Published-at headers))}))

(defn- get-all-pages [fs-root]
  (with-open [children (nio/get-all-files fs-root)]
    (vec (map #(to-page % fs-root) children))))

(defn- sort-by-descending-date [pages]
  (reverse (sort-by :published-at pages)))

(defn build-page-list [fs-root]
  (->> fs-root
       get-all-pages
       sort-by-descending-date
       templates/page-list
       (apply str)))
