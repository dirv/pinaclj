(ns pinaclj.transforms.templated-content
  (:require [pinaclj.page :as page]
            [pinaclj.templates :as templates]
            [pinaclj.link-transform :as link]
            [pinaclj.punctuation-transform :as punctuation]))

(defn- do-template [page opts]
  (-> (assoc page :content ((:template opts) page))
      punctuation/transform
      link/transform
      :content
      templates/to-str
      ))

(defn apply-transform [page]
  (page/set-lazy-value page :templated-content do-template))
