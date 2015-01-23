(ns pinaclj.templates
  (:require [net.cgrand.enlive-html :as html]))


(html/defsnippet page-link "templates/page_list.html"
  [(html/attr= :data-id "page-list-item")]
  [page]
  [(html/attr= :data-id "page-link")] (html/do-> (html/set-attr :href (:url page))
                                   (html/content (:title page))))

(html/deftemplate page-list "templates/page_list.html"
  [pages]
  [[(html/attr= :data-id "page-list")]
   [(html/attr= :data-id "page-list-item")]] (html/clone-for [item pages]
                                                 [(html/attr= :data-id "page-list")] (html/content (page-link item))))

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

(html/deftemplate page "templates/page.html"
  [page]
  [:body] (page-replace page))
