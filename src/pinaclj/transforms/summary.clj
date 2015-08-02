(ns pinaclj.transforms.summary
  (:require [endophile.core :as md]
            [clojure.string :as string]))

(def render-markdown
  (comp md/to-clj md/mp))

(def max-summary-length 200)

(def more-mark "[…]")

(defn- trim-to-first-para [content]
  (first (string/split content #"\n\n" 0)))

(defn- chop [content]
  (subs content 0 (.lastIndexOf content " " (- max-summary-length (count more-mark)))))

(defn- trim-to-max-length [content]
  (if (< max-summary-length (.length content))
    (str (chop content) more-mark)
    content))

(defn to-summary [page opts]
  (-> page
      :raw-content
      trim-to-first-para
      trim-to-max-length
      render-markdown))

(def transform [:summary to-summary])
