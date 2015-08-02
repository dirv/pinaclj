(ns pinaclj.transforms.content
  (:require [endophile.core :as md]))

(def render-markdown
  (comp md/to-clj md/mp))

(defn add-content [page opts]
  (render-markdown (:raw-content page)))

(def transform [:content add-content])
