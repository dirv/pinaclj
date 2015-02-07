(ns pinaclj.test-fs
  (:require [pinaclj.files :as files]
            [pinaclj.templates :as templates])
  (:import (com.google.common.jimfs Jimfs Configuration)))

(defn test-fs []
  (Jimfs/newFileSystem (Configuration/unix)))

(defn create-from [files]
  (let [fs  (files/init (test-fs) "/test")]
    (doseq [file files]
      (files/create (files/resolve-path fs (:path file)) (:content file)))
    fs))

(defn create-empty []
  (create-from []))

(defn resource-stream [path]
  (.getResourceAsStream (clojure.lang.RT/baseLoader) path))
