(ns pinaclj.theme
  (:require [pinaclj.templates :as t]
            [pinaclj.files :as f]))

(defn- load-template [fs root page]
  (t/build-template (f/read-stream fs (str root page))))

(defn get-template [theme n]
  (get theme n))

(defn root-pages [theme]
  (keys (dissoc theme :post.html)))

(defn build-theme [fs path]
  {:post.html (load-template fs path "/post.html")
   :index.html (load-template fs path "/index.html")
   :feed.xml (load-template fs path "/feed.xml")
  })

(def to-template-path
  (comp f/remove-extension f/trim-url))

(defn- find-template? [theme path-str]
  (let [template-key (keyword path-str)]
    (when (get theme template-key)
      template-key)))

(defn determine-template [theme page-path]
  (let [template-path (to-template-path page-path)]
    (or (find-template? theme template-path)
        (find-template? theme (str template-path ".html"))
        :post.html)))
