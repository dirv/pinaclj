(ns pinaclj.read
  (:require [pinaclj.files :as files]
            [pinaclj.date-time :as date-time]
            ))

(defn- separates-headers? [line]
  (= line "---"))

(defn- to-header [line]
  (let [split-pos (.indexOf line ": ")]
    (when (not (= -1 split-pos))
    { (keyword (subs line 0 split-pos)) (subs line (+ 2 split-pos))})))

(defn- split-header-content [all-lines]
  (let [split (split-with (complement separates-headers?) all-lines)]
    [(first split) (clojure.string/join "\n" (rest (second split)))]))

(defn- to-headers [header-section]
  (apply merge (map to-header header-section)))

(defn- convert-published-at [headers]
  (if-let [published-at (:published-at headers)]
    (assoc headers :published-at (date-time/from-str published-at))
    headers))

(defn parse-page [path]
  (let [header-and-content (split-header-content (files/read-lines path))]
    (merge {:raw-content (second header-and-content)
            :path path}
           (to-headers (first header-and-content)))))

(defn read-page [path]
  (-> path
      (parse-page)
      (convert-published-at)))
