(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.nio :as nio]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.group :as group]))

(defn create-page [src path]
  (transforms/apply-all {:path path :src-root src}))

(defn- to-url [page]
  (page/retrieve-value page :destination {}))

(defn- generate-page []
  (assoc (create-page nil nil)
         :modified (System/currentTimeMillis)
         :generated true
         :path "index.md"
         :raw-content ""
         :published-at (dt/now)))

(defn- create-list-page [pages url]
  (assoc (generate-page)
         :pages (map to-url pages)
         :url url))

(def build-list-page
  (comp transforms/apply-all create-list-page))

(defn- build-group-page [[group pages] url-func]
  (assoc (build-list-page pages (url-func group))
         :title (name group)))

(defn- build-group-pages [pages url-func]
  (map #(build-group-page % url-func) pages))

(defn build-tag-pages [pages]
  (build-group-pages (group/pages-by-tag pages) group/tag-url))

(defn build-category-pages [pages]
  (build-group-pages (group/pages-by-category pages) group/category-url))

(defn- split-page-url [page]
  (.split (page/retrieve-value page :destination {}) "\\."))

(defn- create-url [page num]
  (cond
    (= num 0) (:url page)
    (and (> num 0) (< num (dec (count (:pages page)))))
    (let [[start ext] (split-page-url page)]
      (str start "-" (inc num) "." ext))))

(defn- duplicate-page [page start num-pages]
  (let [page-num (/ start num-pages)]
    (assoc page
           :start start
           :raw-content ""
           :url (create-url page page-num)
           :pages (take num-pages (drop start (:pages page)))
           :previous (create-url page (dec page-num))
           :next (create-url page (inc page-num)))))

(defn divide [page {max-pages :max-pages}]
  (if (nil? max-pages)
    [page]
    (let [starts (range 0 (count (:pages page)) max-pages)]
      (map #(duplicate-page page % max-pages) starts))))
