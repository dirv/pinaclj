(ns pinaclj.transforms.summary
  (:require [pinaclj.page :as page]))

(def max-summary-length 100)

(def more-mark "[…]")

(defn- trim-to-space [content]
  (subs content 0 (.lastIndexOf content " " (- max-summary-length (count more-mark)))))

(defn- to-summary [page opts]
  (if (< max-summary-length (.length (:raw-content page)))
    (str (trim-to-space (:raw-content page)) more-mark)
    (:raw-content page)))

(defn add-summary [page]
  (page/set-lazy-value page :summary to-summary))

