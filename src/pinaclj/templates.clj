(ns pinaclj.templates
  (:require [net.cgrand.enlive-html :as html]))

(defn build-link-func [page-obj]
  (html/snippet page-obj
  [(html/attr= :data-id "page-link")]
  [page]
  [(html/attr= :data-id "page-link")] (html/do-> (html/set-attr :href (:url page))
                                   (html/content (:title page)))))

(defn build-list-func [page-obj]
  (let [link-func (build-link-func page-obj)]
    (html/template page-obj [pages]
                   [[(html/attr= :data-id "page-list")] [(html/attr= :data-id "page-list-item")]]
                   (html/clone-for [item pages]
                                   [(html/attr= :data-id "page-list-item")] (html/content (link-func item))))))

(defn- build-replacement-selector [kv]
  [(html/attr= :data-id (name (first kv)))])

(defn- build-replacement-transform [kv]
  (fn [node]
    (assoc node :content (html/html-snippet (second kv)))))

(defn- build-replacement-kv [kv]
  (vec (list (build-replacement-selector kv) (build-replacement-transform kv))))

(defn- build-replacement-list [page]
  (map build-replacement-kv page))

(defn- page-replace [page]
  #(html/at* % (build-replacement-list page)))

(defn build-page-func [page-obj]
  (html/template page-obj [page] [:body] (page-replace page)))
