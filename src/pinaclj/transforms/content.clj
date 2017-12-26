(ns pinaclj.transforms.content
  (:require [pinaclj.markdown :as markdown]))

(defn add-content [page opts]
  (markdown/render-markdown (:raw-content page)))

(def transform [:content add-content])
