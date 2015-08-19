(ns pinaclj.site
  (:require [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(defn- published? [page]
  (some? (:published-at page)))

(defn- modified-since-last-publish? [page dest-last-modified]
  (> (page/retrieve-value page :modified {}) dest-last-modified))

(defn- modified-pages [pages dest-last-modified]
  (filter #(modified-since-last-publish? % dest-last-modified) pages))

(defn- template-page-pair [theme page]
  (vector (theme/determine-template theme page) page))

(defn- generate-list [modified-pages pages theme]
  (concat (map #(template-page-pair theme %) modified-pages)
          (map #(template-page-pair theme %) (pb/build-tag-pages pages))
          (map #(template-page-pair theme %) (pb/build-category-pages pages))
          (map #(vector % (pb/build-list-page pages (name %))) (theme/root-pages theme))))

(defn- final-page [page template]
  [(page/retrieve-value page :destination {})
   (page/retrieve-value page :templated-content {:template template})])

(defn- divide-page [theme [template-name page]]
  (let [page-template (theme/get-template theme template-name)]
    (map #(final-page % page-template) (pb/divide page page-template))))

(defn- divide-pages [pages theme]
  (mapcat #(divide-page theme %) pages))

(defn- published-only [pages]
  (filter published? pages))

(defn- build-published [published-pages theme dest-last-modified]
  (-> published-pages
      (modified-pages dest-last-modified)
      (generate-list published-pages theme)
      (divide-pages theme)))

(defn build [input-pages theme dest-last-modified]
  (build-published (published-only input-pages) theme dest-last-modified))
