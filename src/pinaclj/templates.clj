(ns pinaclj.templates
  (:require [net.cgrand.enlive-html :as html]
            [pinaclj.date-time :as date]
            [pinaclj.page :as page]))

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
  [(html/attr= :data-id (name field))])

(declare page-replace)

(defn- build-replacement-transform [k page]
  (fn [node]
    (let [value (page/retrieve-value page k (renamed-data-attrs node))]
      (cond
        (map? value)
        ((html/clone-for [item (:pages value)]
                         [(html/attr= :data-id "page-list-item")]
                         (page-replace item)) node)
        (seq? value)
        ((html/content value) node)
        :else
        ((html/content (.toString value)) node)))))

(defn- build-replacement-kv [k page]
  (doall (list (build-replacement-selector k)
               (build-replacement-transform k page))))

(defn- build-replacement-list [page]
  (map #(build-replacement-kv % page) (page/all-keys page)))

(defn- page-replace [page]
  #(html/at* % (build-replacement-list page)))

(defn build-page-func [page-obj]
  (html/snippet page-obj [html/root] [page] [html/root] (page-replace page)))

(defn build-link-func [page-obj]
  (html/snippet page-obj
                [(html/attr= :data-id "page-list-item")]
                [page]
                [(html/attr= :data-href "page-link")]
                (html/do-> (html/set-attr :href (:url page)))
                [html/root]
                (page-replace page)
                ))

(defn- find-latest-page [pages]
  (date/to-str (last (sort-by :published-at (map :published-at pages)))))

(defn build-list-func [page-obj link-func]
  (html/snippet page-obj
                [html/root]
                [pages]
                [[(html/attr= :data-id "page-list-item")]]
                (html/clone-for [item pages]
                                [(html/attr= :data-id "page-list-item")] (html/substitute (link-func item)))
                [[(html/attr= :data-id "latest-published-at")]]
                (html/content (find-latest-page pages))
                ))

(defn to-str [nodes]
  (apply str (html/emit* nodes)))
