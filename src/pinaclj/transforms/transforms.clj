(ns pinaclj.transforms.transforms
  (:require [clojure.tools.namespace.find :as find]
            [clojure.java.classpath :as cp]
            [pinaclj.page :as page]))

(defn- transform-ns? [ns]
  (and (.startsWith ns "pinaclj.transforms.")
       (not (.endsWith ns "-spec"))
       (not (.endsWith ns ".transforms"))))

(defn- get-transforms-on-classpath []
  (filter #(transform-ns? (name (.getName %)))
          (find/find-namespaces (cp/classpath))))

(defn- apply-transform [page ns]
  (require ns)
  (if-let [transform (ns-resolve ns 'transform)]
    (apply page/set-lazy-value page (var-get transform))
    page))

(def transforms
  (vec (get-transforms-on-classpath)))

(defn apply-all [page]
  (reduce apply-transform page transforms))
