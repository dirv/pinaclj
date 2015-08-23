(ns pinaclj.transforms.templated-content
  (:require [pinaclj.templates :as templates]
            [pinaclj.link-transform :as link]
            [pinaclj.punctuation-transform :as punctuation]))

(defn- build [page opts]
  (if (and (contains? opts :template)
           (contains? opts :all-pages))
    (((get-in opts [:template :template-fn]) (:all-pages opts)) page)
    ""))

(defn do-template [page opts]
  (-> (assoc page :content (build page opts))
      punctuation/transform
      link/transform
      :content
      templates/to-str))

(def transform [:templated-content do-template])
