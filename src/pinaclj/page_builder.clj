(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.page-list :as page-list]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.group :as group]))

(defn create-page [src path]
  (transforms/apply-all {:path path :src-root src}))

(defn generate-page [url]
  (assoc (create-page nil nil)
         :modified (System/currentTimeMillis)
         :generated true
         :path "index.md"
         :url url
         :raw-content ""
         :published-at (dt/now)))

(defn- build-group-page [[group pages] url-func]
  (assoc (generate-page (url-func group))
         :pages (page/to-page-urls pages)
         :title (name group)))

(defn- build-group-pages [pages url-func]
  (map #(build-group-page % url-func) pages))

(defn build-tag-pages [pages]
  (build-group-pages (group/pages-by-tag pages) group/tag-url))

(defn build-category-pages [pages]
  (build-group-pages (group/pages-by-category pages) group/category-url))

(defn- split-page-url [page]
  (.split (page/retrieve-value page :destination {}) "\\."))

(defn- create-url [page num page-count]
  (cond
    (= num 0) (:url page)
    (and (> num 0) (< num (dec page-count)))
    (let [[start ext] (split-page-url page)]
      (str start "-" (inc num) "." ext))))

(defn- duplicate-page [page start num-pages child-pages]
  (let [page-num (/ start num-pages)
        page-count (count child-pages)]
    (assoc page
           :start start
           :raw-content ""
           :url (create-url page page-num page-count)
           :pages (take num-pages (drop start child-pages))
           :previous (create-url page (dec page-num) page-count)
           :next (create-url page (inc page-num) page-count))))

(defn divide [page {max-pages :max-pages} all-pages]
  (if (nil? max-pages)
    [page]
    (let [child-pages (page-list/children page all-pages)
          starts (range 0 (count child-pages) max-pages)]
      (map #(duplicate-page page % max-pages child-pages) starts))))
