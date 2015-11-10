(ns pinaclj.theme
  (:require [pinaclj.templates :as t]
            [pinaclj.page :as page]
            [pinaclj.nio :as nio]
            [pinaclj.files :as f]))

(def default-category :post)

(def default-category-file-name
  (str (name default-category) ".html"))

(def template-files #{".html" ".xml"})

(defn- load-template [fs root page-path]
  (assoc (t/build-template (f/read-stream fs page-path))
         :modified-at (nio/get-last-modified-time page-path)
         :path (str (nio/relativize root page-path))))

(defn get-template [theme n]
  (get theme n))

(defn root-pages [theme]
  (vals (:templates theme)))

(defn- template-or-other [file]
  (if (contains? template-files (f/extension file))
    :template-files
    :static-files))

(defn- group-files [root]
  (group-by template-or-other (f/all-in root)))

(defn- map-by [f xs]
  (zipmap (map f xs) xs))

(defn- convert-templates [template-files fs root]
  (map-by :path (map #(load-template fs root %) template-files)))

(defn- convert-intermediate [fs path {:keys [template-files static-files]}]
  {:templates (convert-templates template-files fs path)
   :static-files static-files})

(defn build-theme [fs path]
  (convert-intermediate fs path (group-files path)))

(def to-template-path
  (comp f/remove-extension f/trim-url))

(defn- find-template? [{templates :templates} path-str]
  (get templates path-str))

(defn- relativize-template [page]
  (subs (str (:path page))
        (count (str (or (:src-root page) "")))))

(defn matching-template? [theme page]
  (let [template-path (to-template-path (relativize-template page))]
    (or (find-template? theme template-path)
        (find-template? theme (str template-path ".html")))))

(defn- category-template? [theme page]
  (when-let [category (page/retrieve-value page :category)]
    (find-template? theme (str (name category) ".html"))))

(defn- default-category-template [theme]
  (find-template? theme default-category-file-name))

(defn determine-template [theme page]
  (or (matching-template? theme page)
      (category-template? theme page)
      (default-category-template theme)))
