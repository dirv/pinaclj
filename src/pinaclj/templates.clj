(ns pinaclj.templates
  (:require [net.cgrand.enlive-html :as html]
            [pinaclj.page :as page]))

(def page-list-selector
  (html/attr= :data-id "page-list"))

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
  [#{html/root (html/but page-list-selector)} :> (html/attr= :data-id (name field))])

(declare page-replace)

(defn- transform-child-pages [node k {child-pages :pages}]
  (if (nil? child-pages)
    node
    ((html/clone-for [item child-pages]
                   [(html/attr= :data-id (name k))]
                   (page-replace item)) node)))


(defn- transform-nested-page [node {page :page}]
  (if (nil? page)
    node
    ((page-replace page) node)))

(defn- transform-content [node value]
  ((html/content value) node))

(defn- build-replacement-transform [k page]
  (fn [node]
    (let [value (page/retrieve-value page k (renamed-data-attrs node))]
      (cond
        (map? value)
        (-> node
            (transform-child-pages k value)
            (transform-nested-page value))
        (seq? value)
        (transform-content node value)
        :else
        (transform-content node (.toString value))))))

(defn- build-replacement-kv [k page]
  (doall (list (build-replacement-selector k)
               (build-replacement-transform k page))))

(defn- add-link [rs page]
  (cons (list [(html/attr= :data-href "page-link")]
              (html/set-attr :href (:url page))) rs))

(defn- build-replacement-list [page]
  (map #(build-replacement-kv % page) (page/all-keys page)))

(defn- page-replace [page]
  #(html/at* % (add-link (build-replacement-list page) page)))

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
