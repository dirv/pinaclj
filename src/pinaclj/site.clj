(ns pinaclj.site
  (:require [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(defn- published? [page]
  (some? (:published-at page)))

(defn- modified-since-last-publish? [page opts dest-last-modified]
  (> (page/retrieve-value page :modified opts) dest-last-modified))

(defn- template-modified [page dest-last-modified]
  (> (:modified-at (:template page)) dest-last-modified))

(defn- modified-pages [pages opts dest-last-modified]
  (filter #(or (modified-since-last-publish? % opts dest-last-modified)
               (template-modified % dest-last-modified)) pages))

(defn- to-pair [page]
  {(page/retrieve-value page :destination {}) page})

(defn- associate-template [theme page]
  (assoc page :template (theme/determine-template theme page)))

(defn- not-found-pages [theme pages]
  (clojure.set/difference
    (theme/root-pages theme)
    (set (map keyword (page/to-page-urls pages)))))

(defn- post-pages-only [theme pages]
  (remove #(and (theme/matching-template? theme %)
                (nil? (:category %))) pages))

(defn- generate-page-map [pages theme]
  (apply merge (map (comp to-pair (partial associate-template theme))
                    (concat pages
                            (pb/build-tag-pages pages)
                            (pb/build-category-pages (post-pages-only theme pages))
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

(defn render-all [page-map theme dest-last-modified]
  (map #(render-page % page-map)
       (modified-pages (divide-pages page-map) {:all-pages page-map} dest-last-modified)))

(defn- add-has-page-list [theme page-map page]
  (if (and (not (contains? page :pages))
           (:has-page-list? (theme/determine-template theme page)))
    (assoc page :pages (page/children page page-map))
    page))

(defn- add-template-properties [page-map theme]
  (zipmap (keys page-map)
          (map (partial add-has-page-list theme page-map) (vals page-map))))

(defn build [input-pages theme dest-last-modified]
  (-> input-pages
      (published-only)
      (generate-page-map theme)
      (add-template-properties theme)
      (render-all theme dest-last-modified)))
