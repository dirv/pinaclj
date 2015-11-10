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

(defn- associate-template [theme page]
  (assoc page :template (theme/determine-template theme page)))

(defn- build-templated-pages [pages theme]
  (map (partial associate-template theme) pages))

(defn- unused-templates [theme pages]
  (clojure.set/difference (set (theme/root-pages theme))
                          (set (map :template pages))))

(defn- add-unused-template-files [pages theme]
  (let [unused-templates (unused-templates theme pages)
        unused-template-pages (map #(pb/generate-page (:path %)) unused-templates)]
    (concat pages
            (map assoc unused-template-pages (repeat :template) unused-templates))))

(defn- generate-page-map [pages]
  (zipmap (map #(page/retrieve-value % :destination) pages) pages))

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

(defn- children-without-this-page [page all-pages]
  (remove #(= % (page/retrieve-value page :destination))
          (children/children page (:template page) all-pages)))

(defn- set-child-pages [page all-pages]
  (if (:requires-split? (:template page))
    (assoc page :pages (children-without-this-page page all-pages))
    page))

(defn- set-all-child-pages [pages]
  (map #(set-child-pages % pages) pages))

(defn- post-pages-only [theme pages]
  (remove #(and (theme/matching-template? theme %)
                (nil? (:category %))) pages))

(defn- add-generated-pages [pages theme]
  (concat pages
          (build-templated-pages
            (concat (pb/build-tag-pages pages)
                    (pb/build-category-pages (post-pages-only theme pages))) theme)))

(defn build [input-pages theme dest-last-modified]
  (-> input-pages
      (published-only)
      (build-templated-pages theme)
      (add-unused-template-files theme)
      (set-all-child-pages)
      (add-generated-pages theme)
      (generate-page-map)
      (modified-pages-only dest-last-modified)
      (divide-pages)
      (render-pages)))
