(ns pinaclj.core.pages.read
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
    [(first split) (clojure.string/join "\n" (rest (second split)))]))

(defn- to-headers [header-section]
  (apply merge (map to-header header-section)))

(defn- convert-published-at [headers]
  (if-let [published-at-str (:published-at headers)]
    (assoc headers :published-at (string-to-date published-at-str))
    headers))

(defn read-page [path fs-root]
  (let [header-and-content (split-header-content (nio/read-all-lines path))
        headers (to-headers (first header-and-content))]
    (merge {:path (nio/get-path-string fs-root path)
            :content (second header-and-content)}
           (convert-published-at headers))))

(defn- read-all-pages [fs-root]
  (with-open [children (nio/get-all-files fs-root)]
    (vec (map #(read-page % fs-root) children))))
