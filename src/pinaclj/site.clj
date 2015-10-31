(ns pinaclj.site
  (:require [pinaclj.page :as page]
            [pinaclj.children :as children]
            [pinaclj.page-builder :as pb]
            [pinaclj.theme :as theme]))

(defn- modified-since-last-publish? [[_ page] opts dest-last-modified]
  (> (page/retrieve-value page :modified opts) dest-last-modified))

(defn- template-modified [[_ page] dest-last-modified]
  (> (:modified-at (:template page)) dest-last-modified))

(defn- modified-pages-only [pages dest-last-modified]
  (into {} (filter #(or (modified-since-last-publish? % {:all-pages pages} dest-last-modified)
                        (template-modified % dest-last-modified)) pages)))

(defn- to-pair [page]
  {(page/retrieve-value page :destination) page})

(defn- associate-template [theme page]
  (assoc page :template-key (theme/determine-template theme page)))

(defn- unused-template-pages [theme pages]
  (map name (clojure.set/difference (theme/root-pages theme)
                                    (set (map :template-key pages)))))

(defn- not-found-index-page [pages]
  (if (some #{"index.html"} (map #(page/retrieve-value % :destination) pages))
    []
    ["index.html"]))

(defn- not-found-pages [theme pages]
  (concat (unused-template-pages theme pages)
          (not-found-index-page pages)))

(defn- post-pages-only [theme pages]
  (remove #(and (theme/matching-template? theme %)
                (nil? (:category %))) pages))

(defn- build-all-pages [pages theme]
  (map (partial associate-template theme)
       (concat pages
               (pb/build-tag-pages pages)
               (pb/build-category-pages (post-pages-only theme pages)))))

(defn- add-unused-template-files [pages theme]
  (concat pages
          (map (comp (partial associate-template theme) pb/generate-page)
               (not-found-pages theme pages))))

(defn- generate-page-map [pages]
  (into {} (map to-pair pages)))

(defn- divide-page [page-map [_ page :as kv]]
  (into page-map (pb/divide kv (:template page))))

(defn- divide-pages [page-map]
  (reduce divide-page {} page-map))

(defn- published-only [pages]
  (filter :published-at pages))

(defn- update-all [m update-fn & args]
  (zipmap (keys m) (map #(apply update-fn % args) (vals m))))

(defn- render-page [page all-pages]
  (page/retrieve-value page :templated-content {:template (:template page)
                                                :all-pages all-pages}))

(defn- render-pages [pages]
  (update-all pages render-page pages))

(defn- children-without-this-page [page page-map]
  (remove #(= % (page/retrieve-value page :destination))
          (children/children page (:template page) page-map)))

(defn- add-split-pages [page theme page-map]
  (if (and (not (contains? page :pages))
           (:requires-split? (:template page)))
    (assoc page :pages (children-without-this-page page page-map))
    page))

(defn- add-template [pages theme]
  (map #(assoc % :template (get-in theme [:templates (:template-key %)])) pages))

(defn- add-template-properties [page-map theme]
  (update-all page-map add-split-pages theme page-map))

(defn build [input-pages theme dest-last-modified]
  (-> input-pages
      (published-only)
      (build-all-pages theme)
      (add-unused-template-files theme)
      (add-template theme)
      (generate-page-map)
      (add-template-properties theme)
      (modified-pages-only dest-last-modified)
      (divide-pages)
      (render-pages)))
