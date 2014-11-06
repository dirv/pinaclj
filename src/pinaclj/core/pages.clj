(ns pinaclj.core.pages
  (:require [pinaclj.core.nio :as nio]
            [pinaclj.core.templates :as templates])
  (:import (java.time Instant Month ZoneId ZonedDateTime)
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

(defn read-page [path fs-root]
  (let [header-and-content (split-header-content (nio/read-all-lines path))
        headers (to-headers (first header-and-content))]
    {:path (nio/get-path-string fs-root path)
     :content (second header-and-content)
     :title (:Title headers)
     :published-at (string-to-date (:Published-at headers))}))

(defn format-published-at [published-at]
  (.format published-at DateTimeFormatter/ISO_INSTANT))

(defn line [& strings]
  (str (apply str strings) "\n"))

(defn published-at-if-present [page]
  (if-let [published-at (:published-at page)]
    (line "Published-at: " (format-published-at published-at))
    ""))

(defn serialize [page]
  (str (line "Title: " (:title page))
       (published-at-if-present page)
       (line)
       (:content page)))

(defn write-page [path page]
  (nio/create-file path (serialize page)))

(defn- get-all-pages [fs-root]
  (with-open [children (nio/get-all-files fs-root)]
    (vec (map #(read-page % fs-root) children))))

(defn- sort-by-descending-date [pages]
  (reverse (sort-by :published-at pages)))

(defn build-page-list [fs-root]
  (->> fs-root
       get-all-pages
       sort-by-descending-date
       templates/page-list
       (apply str)))
