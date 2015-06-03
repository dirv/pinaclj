(ns pinaclj.read
  (:require [pinaclj.files :as files]
            [pinaclj.date-time :as date-time]
            ))

(defn- separates-headers? [line]
  (= line "---"))

(defn- to-header [line]
  (let [split-pos (.indexOf line ": ")]
    (when (not (= -1 split-pos))
       [(keyword (subs line 0 split-pos))
        (subs line (+ 2 split-pos))])))

(defn- split-header-content [all-lines]
  (let [split (split-with (complement separates-headers?) all-lines)]
    [(first split) (clojure.string/join "\n" (rest (second split)))]))

(defn- convert [[k v]]
  (if (= :published-at k)
    [k (date-time/from-str v)]
    [k v]))

(defn- to-headers [header-section]
  (into {} (map (comp convert to-header) header-section)))

(defn read-page [src-root path]
  (let [header-and-content (split-header-content (files/read-lines path))]
    (assoc (to-headers (first header-and-content))
           :raw-content (second header-and-content)
           :path path
           :src-root src-root)))
