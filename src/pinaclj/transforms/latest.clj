(ns pinaclj.transforms.latest
  (:require [pinaclj.page :as page]))


(defn get-latest [{pages :pages} opts]
  (last (sort-by :published-at pages)))

(defn apply-transform [page-set]
  (page/set-lazy-value page-set :latest get-latest))
