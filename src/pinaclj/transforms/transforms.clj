(ns pinaclj.transforms.transforms
  (:require [clojure.tools.namespace.find :as find]
            [clojure.java.classpath :as cp]))

(defn- transform-ns? [ns]
  (and (.startsWith ns "pinaclj.transforms.")
       (not (.endsWith ns "-spec"))
       (not (.endsWith ns ".transforms"))))

(defn- all-namespaces []
  (find/find-namespaces (cp/classpath)))

(defn- get-transforms []
  (filter #(transform-ns? (name (.getName %)))
          (all-namespaces)))

(defn- do-apply [transform ns]
  (require ns)
  (if-let [nt (ns-resolve ns 'apply-transform)]
    (nt transform)
    transform))

(def transforms
  (vec (get-transforms)))

(defn apply-all [page]
  (reduce do-apply page transforms))
