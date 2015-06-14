(ns pinaclj.transforms.transforms
  (:require [pinaclj.transforms.published-at :as published-at]
            [pinaclj.transforms.summary :as summary]
            [pinaclj.transforms.content :as content]
            [pinaclj.transforms.url :as url]
            [pinaclj.transforms.destination :as destination]))

(defn apply-all [page]
  (-> page
      (published-at/apply-transform)
      (content/apply-transform)
      (summary/apply-transform)
      (url/apply-transform)
      (destination/apply-transform)))

