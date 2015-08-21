(ns pinaclj.transforms.if-exists
  (:require [pinaclj.page :as page]))

(defn- is-null-or-empty [v]
  (or (= nil v) (= "" v)))

(defn if-exists [page opts]
  (if (is-null-or-empty (page/retrieve-value page (keyword (:key opts)) {}))
    {:delete nil}
    {}))

(def transform [:if-exists if-exists])
