(ns pinaclj.transforms.summary
  (:require [pinaclj.page :as page]
            [endophile.core :as md]))

(def render-markdown
  (comp md/to-clj md/mp))

(def max-summary-length 200)

(def more-mark "[…]")

(defn- trim-to-space [content]
  (subs content 0 (.lastIndexOf content " " (- max-summary-length (count more-mark)))))

(defn- trim-summary [page]
  (if (< max-summary-length (.length (:raw-content page)))
    (str (trim-to-space (:raw-content page)) more-mark)
    (:raw-content page)))

(defn- to-summary [page opts]
  (render-markdown (trim-summary page)))

(defn apply-transform [page]
  (page/set-lazy-value page :summary to-summary))

