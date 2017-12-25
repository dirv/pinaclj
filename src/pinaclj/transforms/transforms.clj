(ns pinaclj.transforms.transforms
  (:require [clojure.tools.namespace.find :as find]
            [pinaclj.page :as page]))

(defn get-classpath []
  (map #(clojure.java.io/file %)
       (clojure.string/split (System/getProperty "java.class.path")
                             (re-pattern java.io.File/pathSeparator))))

(defn- transform-ns? [this-ns]
  (and (.startsWith this-ns "pinaclj.transforms.")
       (not (.endsWith this-ns "-spec"))
       (not (.endsWith this-ns ".transforms"))))

(defn- get-transforms-on-classpath []
  (filter #(transform-ns? (name (.getName %)))
          (find/find-namespaces (get-classpath))))

(defn- apply-transform [page this-ns]
  (require this-ns)
  (if-let [transform (ns-resolve this-ns 'transform)]
    (apply page/set-lazy-value page (var-get transform))
    page))

(def transforms
  (vec (get-transforms-on-classpath)))

(defn apply-all [page]
  (reduce apply-transform page transforms))
