(ns pinaclj.transforms.page-link
  (:require [pinaclj.page :as page]
            [clojure.string :as s]))

(defn- matches-title [expected actual-kv]
  (when-let [title (:title (val actual-kv))]
    (= expected (s/lower-case title))))

(defn- find-page [page {all-pages :all-pages page-key :key}]
  (when-let [title (page/retrieve-value page (keyword page-key) {})]
    (if-let [match (first (filter (partial matches-title (s/lower-case title)) all-pages))]
      (val match))))

(defn- determine-page [page opts]
  (if (contains? opts :key)
    (find-page page opts)
    page))

(defn- build-page-link [page]
  {:attrs {:href (page/retrieve-value page :destination {})}
   :content (:title page)})

(defn set-page-link [page opts]
  (build-page-link (determine-page page opts)))

(def transform [:page-link set-page-link])
