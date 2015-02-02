(ns pinaclj.read
  (:require [pinaclj.files :as files]
            [pinaclj.templates :as templates]
            [pinaclj.date-time :as date-time]))

(defn- separates-headers? [line]
  (= line "---"))

(defn- to-header [line]
  (let [headed-line (clojure.string/split line #": ")]
    { (keyword (first headed-line)) (second headed-line)}))

(defn- split-header-content [all-lines]
  (let [split (split-with (complement separates-headers?) all-lines)]
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
    (merge {:content (second header-and-content)}
           (convert-published-at headers))))

(defn- read-all-pages [path]
  (map read-page (files/all-in path)))
