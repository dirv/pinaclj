(ns pinaclj.core.pages.read
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.templates :as templates]
            [pinaclj.core.pages.date-time :as date-time]))

(def header-separator
  "")

(defn- to-header [line]
  (let [headed-line (clojure.string/split line #": ")]
    { (keyword (first headed-line)) (second headed-line)}))

(defn- split-header-content [all-lines]
  (let [split (split-with #(not (= header-separator %)) all-lines)]
    [(first split) (clojure.string/join "\n" (rest (second split)))]))

(defn- to-headers [header-section]
  (apply merge (map to-header header-section)))

(defn- convert-published-at [headers]
  (if-let [published-at (:published-at headers)]
    (assoc headers :published-at (date-time/from-str published-at))
    headers))

(defn read-page [path]
  (let [header-and-content (split-header-content (files/read-lines path))
        headers (to-headers (first header-and-content))]
    (merge {:path (files/get-path-string path)
            :content (second header-and-content)}
           (convert-published-at headers))))

(defn- read-all-pages []
  (with-open [children (files/all)]
    (vec (map read-page children))))
