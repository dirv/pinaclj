(ns pinaclj.templates
  (:require [net.cgrand.enlive-html :as html]
            [pinaclj.page :as page]))

(def nested-fields #{:page-list :tag-list :latest})

(defn- parent-nodes [pred]
  (html/zip-pred
    (fn [loc]
      (some pred (drop 1 (drop-last (take-while identity (iterate clojure.zip/up loc))))))))

(def nested-selector
  (-> (map #(html/attr= :data-id (name %)) nested-fields)
      html/union
      parent-nodes
      html/but))

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
  [[(html/attr= :data-id (name field)) nested-selector]])

(declare page-replace)

(defmulti transform (fn [node k value all-pages template-keys] (key value)))

(defmethod transform :pages [node k child-pages all-pages template-keys]
  (html/at node
           [html/root :> html/first-child]
           (html/clone-for [item (val child-pages)]
                           (page-replace (get all-pages item) all-pages template-keys))))

(defmethod transform :page [node k page all-pages template-keys]
  ((page-replace (get all-pages (val page)) all-pages template-keys) node))

(defmethod transform :attrs [node k attrs all-pages template-keys]
  (reduce #((html/set-attr (first %2) (second %2)) %1)
          node
          (val attrs)))

(defmethod transform :delete [node k attrs all-pages template-keys])

(defmethod transform :content [node k content all-pages template-keys]
  ((html/content (val content)) node))

(defn- transform-content [node content]
  (if (seq? content)
    ((html/content content) node)
    ((html/content (.toString content)) node)))

(defn- build-opts [node all-pages]
  (merge {:all-pages all-pages} (renamed-data-attrs node)))

(defn- build-replacement-transform [k page all-pages template-keys]
  (fn [node]
    (if-let [value (page/retrieve-value page k (build-opts node all-pages))]
      (if (map? value)
        (reduce #(transform %1 k %2 all-pages template-keys) node value)
        (transform-content node value))
      node)))

(defn- find-all-functions [template]
  (map #(keyword (get-in % [:attrs :data-id])) (html/select template [(html/attr? :data-id)])))

(defn- build-replacement-kv [k page all-pages template-keys]
  (doall (list (build-replacement-selector k)
               (build-replacement-transform k page all-pages template-keys))))

(defn- build-replacement-list [page all-pages template-keys]
  (map #(build-replacement-kv % page all-pages template-keys) template-keys))

(defn- page-replace [page all-pages template-keys]
  #(html/at* % (build-replacement-list page all-pages template-keys)))

(defn build-page-func [template all-pages]
  (html/snippet template
                [html/root]
                [page]
                [html/root]
                (page-replace page all-pages (find-all-functions template))))

(defn- convert-max-page-str [page]
  (if (contains? page :max-pages)
    (assoc page :max-pages (Integer/parseInt (:max-pages page)))
    page))

(defn build-page-list-opts [page]
  (let [node (html/select page [(html/attr= :data-id "page-list")])]
    (when (seq? node)
      (convert-max-page-str (renamed-data-attrs (first node))))))

(defn build-template [page-stream]
  (let [page-resource (html/html-resource page-stream)]
    (assoc (build-page-list-opts page-resource)
           :template-fn (partial build-page-func page-resource))))

(defn to-str [nodes]
  (apply str (html/emit* nodes)))
