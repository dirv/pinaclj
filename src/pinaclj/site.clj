(ns pinaclj.site
  (:require [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(defn- published? [page]
  (some? (:published-at page)))

(defn- modified-since-last-publish? [page opts dest-last-modified]
  (or (:generated page)
    (> (page/retrieve-value page :modified opts) dest-last-modified)))

(defn- modified-pages [pages opts dest-last-modified]
  (filter #(modified-since-last-publish? % opts dest-last-modified) pages))

(defn- to-pair [page]
  {(page/retrieve-value page :destination {}) page})

(defn- associate-template [theme page]
  (assoc page :template (theme/determine-template theme page)))

(defn- not-found-pages [theme pages]
  (clojure.set/difference
    (theme/root-pages theme)
    (set (map keyword (page/to-page-urls pages)))))

(defn- generate-page-map [pages theme]
  (apply merge (map (comp to-pair (partial associate-template theme))
                    (concat pages
                            (pb/build-tag-pages pages)
                            (pb/build-category-pages pages)
                            (map (comp pb/generate-page name) (not-found-pages theme pages))))))

(defn- divide-page [page-map [destination page]]
  (pb/divide page (:template page) page-map))

(defn- divide-pages [page-map]
  (mapcat #(divide-page page-map %) page-map))

(defn- published-only [pages]
  (filter published? pages))

(defn- render-page [page all-pages]
  [(page/retrieve-value page :destination {})
   (page/retrieve-value page :templated-content {:template (:template page)
                                                 :all-pages all-pages})])

(defn render-all [page-map dest-last-modified]
  (map #(render-page % page-map)
       (modified-pages (divide-pages page-map) {:all-pages page-map} dest-last-modified)))

(defn build [input-pages theme dest-last-modified]
  (-> input-pages
      (published-only)
      (generate-page-map theme)
      (render-all dest-last-modified)))
