(ns pinaclj.core.pages.write
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.templates :as templates]
            [pinaclj.core.pages.date-time :as date-time]))

(defmulti serialize-header (fn [pair] (first pair)))

(defmethod serialize-header :published-at [pair]
  (str "published-at: " (date-time/to-str (second pair)) "\n"))

(defmethod serialize-header :default [pair]
  (str (name (first pair)) ": " (second pair) "\n"))

(defn- serialize-headers [headers]
  (apply str (map serialize-header (vec headers))))

(defn- serialize [{:keys [headers content]}]
  (str (serialize-headers headers)
       "\n"
       content))

(defn write-page [path page]
  (files/create path (serialize page)))
