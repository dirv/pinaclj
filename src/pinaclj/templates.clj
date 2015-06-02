(ns pinaclj.templates
  (:import (java.time ZonedDateTime))
  (:require [net.cgrand.enlive-html :as html]
            [pinaclj.date-time :as date]
            [pinaclj.page :as page]))

(defn- build-replacement-selector [field]
  [(html/attr= :data-id (name field))])

(defn- build-replacement-transform [k page]
  (let [value (page/retrieve-value page k {})]
    (if (instance? ZonedDateTime value)
      (html/content (.toString value))
      (html/content value))))

(defn- build-replacement-kv [k page]
  (doall (list (build-replacement-selector k)
               (build-replacement-transform k page))))

(defn- build-replacement-list [page]
  (map #(build-replacement-kv % page) (page/all-keys page)))

(defn- page-replace [page]
  #(html/at* % (build-replacement-list page)))

(defn build-page-func [page-obj]
  (html/snippet page-obj [html/root] [page] [html/root] (page-replace page)))

(defn build-link-func [page-obj]
  (html/snippet page-obj
                [(html/attr= :data-id "page-list-item")]
                [page]
                [(html/attr= :data-href "page-link")]
                (html/do-> (html/set-attr :href (:url page)))
                [html/root]
                (page-replace page)
                ))

(defn- find-latest-page [pages]
  (date/to-str (last (sort-by :published-at (map :published-at pages)))))

(defn build-list-func [page-obj link-func]
  (html/snippet page-obj
                [html/root]
                [pages]
                [[(html/attr= :data-id "page-list-item")]]
                (html/clone-for [item pages]
                                [(html/attr= :data-id "page-list-item")] (html/substitute (link-func item)))
                [[(html/attr= :data-id "latest-published-at")]]
                (html/content (find-latest-page pages))
                ))

(defn to-str [nodes]
  (apply str (html/emit* nodes)))
