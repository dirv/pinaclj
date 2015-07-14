(ns pinaclj.theme
  (:require [pinaclj.templates :as t]
            [pinaclj.files :as f]))

(defn- load-template [fs root page]
  (t/build-page-func (f/read-stream fs (str root page))))

(defn get-template [theme n]
  (get theme n))

(defn root-pages [theme]
  (keys (dissoc theme :post)))

(defn build-theme [fs path]
  {:post (load-template fs path "/post.html")
   :index.html (load-template fs path "/index.html")
   :feed.xml (load-template fs path "/feed.xml")
   })
