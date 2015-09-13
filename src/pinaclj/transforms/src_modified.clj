(ns pinaclj.transforms.src-modified
  (:require [pinaclj.nio :as nio]))

(defn- get-last-modified-time [page opts]
  (if-not (:generated page)
    (nio/get-last-modified-time (:path page))
    0))

(def transform [:src-modified get-last-modified-time])



