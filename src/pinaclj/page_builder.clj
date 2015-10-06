(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.group :as group]))

(defn create-page [src path]
  (transforms/apply-all {:path path :src-root src}))

(defn generate-page [url]
  (assoc (create-page nil nil)
         :modified (System/currentTimeMillis)
         :parent "index.html"
         :generated true
         :path "index.md"
         :url url
         :raw-content ""
         :published-at (dt/now)))

(defn- chronological-sort [pages]
  (sort-by #(:published-at %) pages))

(defn- build-group-page [[group pages] url-func category]
  (assoc (generate-page (url-func group))
         :category category
         :pages (page/to-page-urls (chronological-sort pages))
         :title (name group)))

(defn- build-group-pages [pages url-func category]
  (map #(build-group-page % url-func category) pages))

(defn build-tag-pages [pages]
  (build-group-pages (group/pages-by-tag pages) page/tag-url "tags"))

(defn build-category-pages [pages]
  (build-group-pages (group/pages-by-category pages) page/category-url "category"))

(defn- split-page-url [page]
  (.split (page/retrieve-value page :destination {}) "\\."))

(defn- build-url-fn [page page-count]
  (let [[start ext] (split-page-url page)]
    (fn [page-num]
      (cond
        (= page-num 0) (page/retrieve-value page :destination {})
        (and (> page-num 0) (< page-num page-count))
        (str start "-" (inc page-num) "." ext)))))

(defn- duplicate-page [page start num-pages child-pages url-fn]
  (let [page-num (/ start num-pages)]
    (assoc page
           :start start
           :raw-content ""
           :url (url-fn page-num)
           :pages (take num-pages (drop start child-pages))
           :next (url-fn (dec page-num))
           :prev (url-fn (inc page-num)))))

(defn divide [page {max-pages :max-pages} all-pages]
  (if (or (nil? max-pages) (empty? (:pages page)))
    [page]
    (let [child-pages (:pages page)
          starts (range 0 (count child-pages) max-pages)
          url-fn (build-url-fn page (count starts))]
      (map #(duplicate-page page % max-pages child-pages url-fn) starts))))
