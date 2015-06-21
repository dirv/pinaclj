(ns pinaclj.transforms.page-list
  (:require [pinaclj.page :as page]
            [net.cgrand.enlive-html :as html]))

(defn- clone-pages [page-set opts]
  page-set)

(defn apply-transform [page-set]
  (page/set-lazy-value page-set :page-list clone-pages))
