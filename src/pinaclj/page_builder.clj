(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.category-page :as cp]
            [pinaclj.tag-page :as tp]))

(defn create-page [src path]
  (transforms/apply-all {:path path :src-root src}))

(defn- create-list-page [pages url]
  {:pages pages
   :raw-content ""
   :path "/index.md"
   :url url
   :modified (System/currentTimeMillis)
   :published-at (dt/now)})

(defn build-list-page [pages url]
  (transforms/apply-all (create-list-page pages url)))

(defn- build-tag-page [[tag pages]]
  (assoc (build-list-page pages (tp/tag-url tag))
         :title (name tag)))

(defn- build-category-page [[category pages]]
  (assoc (build-list-page pages (cp/category-url category))
         :title (name category)))

(defn build-tag-pages [pages]
  (map build-tag-page (tp/pages-by-tag pages)))

(defn build-category-pages [pages]
  (map build-category-page (cp/pages-by-category pages)))

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
