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

(defn- format-published-at [published-at]
  (.format published-at DateTimeFormatter/ISO_INSTANT))

(defmulti serialize-header-pair (fn [pair] (first pair)))

(defmethod serialize-header-pair :published-at [pair]
  (str "published-at: " (format-published-at (second pair)) "\n"))

(defmethod serialize-header-pair :default [pair]
  (str (name (first pair)) ": " (second pair) "\n"))

(defn- serialize-headers [headers]
  (apply str (map serialize-header-pair (vec headers))))

(defn- serialize [{:keys [headers content]}]
  (str (serialize-headers headers)
       "\n"
       content))

(defn write-page [path page]
  (nio/create-file path (serialize page)))

(defn- get-all-pages [fs-root]
  (with-open [children (nio/get-all-files fs-root)]
    (vec (map #(read-page % fs-root) children))))

(defn- sort-by-descending-date [pages]
  (reverse (sort-by :published-at pages)))

(defn build-page [path fs-root]
  (apply str (templates/page (read-page path fs-root))))

(defn build-page-list [fs-root]
  (->> fs-root
       get-all-pages
       sort-by-descending-date
       templates/page-list
       (apply str)))
