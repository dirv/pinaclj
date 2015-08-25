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
  [[(html/attr= :data-id field) nested-selector]])

(declare page-replace)

(defmulti transform (fn [node value all-pages selector-transforms] (key value)))

(defmethod transform :pages [node child-pages all-pages selector-transforms]
  (html/at node
           [html/root :> html/first-child]
           (html/clone-for [item (val child-pages)]
                           (page-replace (get all-pages item) selector-transforms))))

(defmethod transform :page [node page all-pages selector-transforms]
  ((page-replace (get all-pages (val page)) selector-transforms) node))

(defmethod transform :attrs [node attrs all-pages selector-transforms]
  (reduce #((html/set-attr (first %2) (second %2)) %1)
          node
          (val attrs)))

(defmethod transform :delete [node attrs all-pages selector-transforms])

(defmethod transform :content [node content all-pages selector-transforms]
  ((html/content (val content)) node))

(defn- transform-content [node content]
  (if (seq? content)
    ((html/content content) node)
    ((html/content (.toString content)) node)))

(defn- build-opts [node all-pages]
  (merge {:all-pages all-pages} (renamed-data-attrs node)))

(defn- build-replacement-transform [field all-pages]
  (fn [page selector-transforms]
    (fn [node]
      (if-let [value (page/retrieve-value page field (build-opts node all-pages))]
        (if (map? value)
          (reduce #(transform %1 %2 all-pages selector-transforms) node value)
          (transform-content node value))
        node))))

(defn- find-all-functions [template]
  (map #(get-in % [:attrs :data-id]) (html/select template [(html/attr? :data-id)])))

(defn- build-selector-transforms [template all-pages]
  (map #(vector (build-replacement-selector %)
                (build-replacement-transform (keyword %) all-pages)) (find-all-functions template)))

(defn- fix-page [page selector-transforms [selector transform-fn]]
  [selector (transform-fn page selector-transforms)])

(defn- page-replace [page selector-transforms]
  #(html/at* % (map (partial fix-page page selector-transforms) selector-transforms)))

(defn build-page-func [template all-pages]
  (html/snippet template
                [html/root]
                [page]
                [html/root]
                (page-replace page (build-selector-transforms template all-pages))))

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
