(ns pinaclj.transforms.transforms
  (:require [pinaclj.transforms.published-at :as published-at]
            [pinaclj.transforms.tags :as tags]
            [pinaclj.transforms.latest-published-at :as latest-published-at]
            [pinaclj.transforms.summary :as summary]
            [pinaclj.transforms.content :as content]
            [pinaclj.transforms.url :as url]
            [pinaclj.transforms.destination :as destination]
            [pinaclj.transforms.templated-content :as template]
            [pinaclj.transforms.page-list :as page-list]
            [pinaclj.transforms.tag-list :as tag-list]
            [pinaclj.transforms.modified :as modified]
            [pinaclj.transforms.latest :as latest]))

(defn apply-all [page]
  (-> page
      (tags/apply-transform)
      (published-at/apply-transform)
      (latest-published-at/apply-transform)
      (content/apply-transform)
      (summary/apply-transform)
      (url/apply-transform)
      (destination/apply-transform)
      (template/apply-transform)
      (page-list/apply-transform)
      (tag-list/apply-transform)
      (modified/apply-transform)
      (latest/apply-transform)))
