(ns pinaclj.theme
  (:require [pinaclj.templates :as t]
            [pinaclj.page :as page]
            [pinaclj.nio :as nio]
            [pinaclj.files :as f]))

(def default-category :post)

(def template-files #{".html" ".xml"})

(defn- category-template [category]
  (keyword (str (name category) ".html")))

(defn- load-template [fs page-path]
  (assoc (t/build-template (f/read-stream fs page-path))
         :modified-at (nio/get-last-modified-time page-path)))

(defn get-template [theme n]
  (get theme n))

(defn root-pages [theme]
  (clojure.set/difference
    (set (keys (:templates theme)))
    #{(category-template default-category)}))

(defn- to-template [fs root file-path]
  {(keyword (.toString (nio/relativize root file-path)))
   (load-template fs file-path)})

(defn- template-or-other [file]
  (if (contains? template-files (f/extension file))
    :template-files
    :static-files))

(defn- group-files [root]
  (group-by template-or-other (f/all-in root)))

(defn- convert-templates [template-files fs root]
  (apply merge (map #(to-template fs root %) template-files)))

(defn- convert-intermediate [fs path {:keys [template-files static-files]}]
  {:templates (convert-templates template-files fs path)
   :static-files static-files})

(defn build-theme [fs path]
  (convert-intermediate fs path (group-files path)))

(def to-template-path
  (comp f/remove-extension f/trim-url))

(defn- find-template? [{templates :templates} path-str]
  (get templates (keyword path-str)))

(defn- relativize-template [page]
  (subs (.toString (:path page))
        (count (.toString (or (:src-root page) "")))))

(defn matching-template? [theme page]
  (let [template-path (to-template-path (relativize-template page))]
    (or (find-template? theme template-path)
        (find-template? theme (str template-path ".html")))))

(defn- category-template? [theme page]
  (when-let [category (page/retrieve-value page :category {})]
    (find-template? theme (str (name category) ".html"))))

(defn determine-template [theme page]
  (or (matching-template? theme page)
      (category-template? theme page)
      (get (:templates theme) (category-template default-category))))
