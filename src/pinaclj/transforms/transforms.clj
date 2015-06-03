(ns pinaclj.transforms.transforms
  (:require [pinaclj.transforms.published-at-str :as published-at-str]
            [pinaclj.transforms.summary :as summary]
            [pinaclj.transforms.content :as content]))

(defn apply-all [page]
  (-> page
      (published-at-str/apply-transform)
      (content/apply-transform)
      (summary/add-summary)))

