(ns pinaclj.templates
  (:require [net.cgrand.enlive-html :as html]
            [pinaclj.page :as page]))

(def page-list-selector
  (html/attr= :data-id "page-list"))

(def tag-list-selector
  (html/attr= :data-id "tag-list"))

(def page-or-tag-list-selector
  #{page-list-selector tag-list-selector})

(def data-prefix "data-")

(def data-prefix-pattern
  (re-pattern (str data-prefix ".*")))

(defn- remove-data-prefix [attr-name]
  (subs (name (first attr-name)) (count data-prefix)))

(defn- data-attrs [node]
  (filter #(re-matches data-prefix-pattern (name (key %))) (:attrs node)))

(defn- renamed-data-attrs [node]
  (reduce #(assoc %1 (keyword (remove-data-prefix %2)) (second %2))
          {}
          (data-attrs node)))

(defn- build-replacement-selector [field]
  [#{html/root (html/but page-or-tag-list-selector)} :> (html/attr= :data-id (name field))])

(declare page-replace)

(defmulti transform (fn [node k value] (key value)))

(defmethod transform :pages [node k child-pages]
  ((html/clone-for [item (val child-pages)]
                   [(html/attr= :data-id (name k))]
                   (page-replace item)) node))

(defmethod transform :page [node k page]
  ((page-replace (val page)) node))

(defmethod transform :attrs [node k attrs]
  ((html/set-attr (first (first (val attrs))) (second (first (val attrs)))) node))

(defmethod transform :content [node k content]
  ((html/content (val content)) node))

(defn- transform-content [node content]
  (if (seq? content)
    ((html/content content) node)  
    ((html/content (.toString content)) node)))

(defn- build-replacement-transform [k page]
  (fn [node]
    (let [value (page/retrieve-value page k (renamed-data-attrs node))]
      (if (map? value)
        (reduce #(transform %1 k %2) node value)
        (transform-content node value)))))

(defn- build-replacement-kv [k page]
  (doall (list (build-replacement-selector k)
               (build-replacement-transform k page))))

(defn- build-replacement-list [page]
  (map #(build-replacement-kv % page) (page/all-keys page)))

(defn- page-replace [page]
  #(html/at* % (build-replacement-list page)))

(defn- build-page-func [page-obj]
  (html/snippet page-obj [html/root] [page] [html/root] (page-replace page)))

(defn- convert-max-page-str [page]
  (if (contains? page :max-pages)
    (assoc page :max-pages (Integer/parseInt (:max-pages page)))
    page))

(defn- build-page-list-opts [page]
  (let [node (html/select page [page-list-selector])]
    (when (seq? node)
      (convert-max-page-str (renamed-data-attrs (first node))))))

(defn build-template [page-stream]
  (let [page-resource (html/html-resource page-stream)]
    (assoc (build-page-list-opts page-resource)
           :template-func (build-page-func page-resource)
           :page-resource page-resource)))

(defn to-str [nodes]
  (apply str (html/emit* nodes)))
