(ns pinaclj.core.pages.read
  (:require [pinaclj.core.nio :as nio]
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

(defn read-page [fs-root path]
  (let [header-and-content (split-header-content (nio/read-all-lines fs-root path))
        headers (to-headers (first header-and-content))]
    (merge {:path (nio/get-path-string fs-root path)
            :content (second header-and-content)}
           (convert-published-at headers))))

(defn- read-all-pages [fs-root]
  (with-open [children (nio/get-all-files fs-root)]
    (vec (map #(read-page % fs-root) children))))
