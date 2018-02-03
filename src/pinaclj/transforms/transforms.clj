(ns pinaclj.transforms.transforms
  (:require [clojure.tools.namespace.find :as find]
            [pinaclj.page :as page]
            [clojure.set]))

(defn- transform-ns? [this-ns]
  (and (.startsWith this-ns "pinaclj.transforms.")
       (not (.endsWith this-ns "-spec"))
       (not (.endsWith this-ns ".transforms"))))

(defn- get-transforms-on-classpath []
  (filter #(transform-ns? (name (.getName %)))
          (find/find-namespaces-in-dir (java.io.File. "."))))

(defn- apply-transform [page this-ns]
  (require this-ns)
  (if-let [transform (ns-resolve this-ns 'transform)]
    (apply page/set-lazy-value page (var-get transform))
    page))

(def transforms
  (vec (get-transforms-on-classpath)))

(defn apply-all [page]
  (reduce apply-transform page transforms))
