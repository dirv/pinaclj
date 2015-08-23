(ns pinaclj.site
  (:require [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(defn- published? [page]
  (some? (:published-at page)))

(defn- modified-since-last-publish? [[destination template page] opts dest-last-modified]
  (or (:generated page)
    (> (page/retrieve-value page :modified opts) dest-last-modified)))

(defn- modified-pages [pages opts dest-last-modified]
  (filter #(modified-since-last-publish? % opts dest-last-modified) pages))

(defn- template-page-pair [theme page]
  (vector (theme/determine-template theme page) page))

(defn- generate-page-pairs [pages theme]
  (concat (map #(template-page-pair theme %) pages)
          (map #(template-page-pair theme %) (pb/build-tag-pages pages))
          (map #(template-page-pair theme %) (pb/build-category-pages pages))
          (map #(vector % (pb/build-list-page pages (name %))) (theme/root-pages theme))))

(defn- final-page [page template]
  [(page/retrieve-value page :destination {}) template page])

(defn- divide-page [theme [template-name page]]
  (let [page-template (theme/get-template theme template-name)]
    (map #(final-page % page-template) (pb/divide page page-template))))

(defn- divide-pages [pages theme]
  (mapcat #(divide-page theme %) pages))

(defn- published-only [pages]
  (filter published? pages))

(defn- render-page [[destination template page] all-pages]
  [destination
   (page/retrieve-value page :templated-content {:template template :all-pages all-pages})])

(defn- page-map [pages]
  (apply merge (map #(hash-map (first %) (nth % 2)) pages)))

(defn render-all [pages dest-last-modified]
  (let [page-map (page-map pages)]
    (map #(render-page % page-map)
         (modified-pages pages {:all-pages page-map} dest-last-modified))))

(defn- build-published [published-pages theme dest-last-modified]
  (-> published-pages
      (generate-page-pairs theme)
      (divide-pages theme)
      (render-all dest-last-modified)))

(defn build [input-pages theme dest-last-modified]
  (build-published (published-only input-pages) theme dest-last-modified))
