(ns pinaclj.link-transform
  (:require [pinaclj.page :as page]))

(defn relative-url? [url]
  (not (re-find #"\/\/" url)))

(defn- up-dir-str [page-depth]
  (apply str (repeat page-depth "../")))

(defn- convert-url [url page-depth]
  (if (relative-url? url)
    (str (up-dir-str page-depth) url)
    url))

(defn- convert-attr [attrs attr page-depth]
  (if-let [url (get attrs attr)]
    (assoc attrs attr (convert-url (get attrs attr) page-depth))
    attrs))

(defn- convert-attrs [attrs page-depth]
  (-> attrs
      (convert-attr :src page-depth)
      (convert-attr :href page-depth)))

(defn- convert-urls [node page-depth]
  (cond
    (map? node)
      (assoc node
             :attrs (convert-attrs (:attrs node) page-depth)
             :content (convert-urls (:content node) page-depth))
    (or (seq? node) (vector? node))
      (doall (map #(convert-urls % page-depth) node))
    :else
      node))

(defn- page-depth [page]
  (count (filter #(= \/ %) (page/retrieve-value page :url {}))))

(defn transform [page]
  (let [page-depth (page-depth page)]
    (if (pos? page-depth)
      (assoc page :content (convert-urls (:content page) page-depth))
      page)))
