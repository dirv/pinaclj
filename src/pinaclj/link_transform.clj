(ns pinaclj.link-transform
  (:require [pinaclj.page :as page]))

(defn- relative-url? [url]
  (not (re-find #"\/\/" url)))

(defn- up-dir-str [page-depth]
  (clojure.string/join (repeat page-depth "../")))

(defn- convert-url [url page-depth]
  (if (relative-url? url)
    (str (up-dir-str page-depth) url)
    url))

(defn- convert-attr [attrs attr page-depth]
  (if-let [url (get attrs attr)]
    (update-in attrs [attr] convert-url page-depth)
    attrs))

(defn- convert-attrs [attrs page-depth]
  (-> attrs
      (convert-attr :src page-depth)
      (convert-attr :href page-depth)))

(defmulti convert-urls (fn [node page-depth] (class node)))

(defmethod convert-urls clojure.lang.PersistentStructMap [node page-depth]
  (assoc node
         :attrs (convert-attrs (:attrs node) page-depth)
         :content (convert-urls (:content node) page-depth)))

(defmethod convert-urls clojure.lang.PersistentArrayMap [node page-depth]
  (assoc node
         :attrs (convert-attrs (:attrs node) page-depth)
         :content (convert-urls (:content node) page-depth)))

(defmethod convert-urls clojure.lang.LazySeq [node page-depth]
  (map #(convert-urls % page-depth) node))

(defmethod convert-urls :default [node page-depth]
  node)

(defn- page-depth [page]
  (count (filter #(= \/ %) (page/retrieve-value page :destination))))

(defn transform [page]
  (let [page-depth (page-depth page)]
    (if (pos? page-depth)
      (update-in page [:content] convert-urls page-depth)
      page)))
