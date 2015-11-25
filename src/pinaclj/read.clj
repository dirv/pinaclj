(ns pinaclj.read
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.date-time :as date-time]))

(defn- contains-title? [headers]
  (contains? headers :title))

(defn- convert [[k v]]
  (if (= :published-at k)
    [k (date-time/from-str v)]
    [k v]))

(defn- to-header [line]
  (let [split-pos (.indexOf line ": ")]
    (when-not (= -1 split-pos)
       [(keyword (subs line 0 split-pos))
        (subs line (+ 2 split-pos))])))

(defn- to-headers [header-section]
  (into {} (map (comp convert to-header) header-section)))

(defn- merge-page [page [header content]]
  (let [headers (to-headers header)]
    (if (contains-title? headers)
      (assoc (merge page headers)
           :read-headers (vec (keys headers))
           :raw-content content)
      {:result :invalid-source-file :errors [:no-title]})))

(def separator "---")

(defn- line-is-separator? [line]
  (= line separator))

(defn- split-header-content [all-lines]
  (let [split (split-with (complement line-is-separator?) all-lines)]
    [(first split) (clojure.string/join "\n" (rest (second split)))]))

(defn- contains-separator? [all-lines]
  (some #{separator} all-lines))

(defn read-page [page]
  (let [all-lines (files/read-lines (:path page))]
    (cond
      (contains-separator? all-lines)
      (merge-page page (split-header-content all-lines))
      :else {:result :invalid-source-file :errors [:no-separator]})))
