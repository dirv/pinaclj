(ns pinaclj.transforms.content
  (:require [endophile.core :as md]
            [pinaclj.page :as page]))

(def render-markdown
  (comp md/to-clj md/mp))

(defn- add-content [page opts]
  (render-markdown (:raw-content page)))

(defn apply-transform [page]
  (page/set-lazy-value page :content add-content))

