(ns flow.core.nio
  (:import (java.nio.file FileSystems Files LinkOption)
           (java.nio.charset StandardCharsets)))

(defn child-path [fs-root path]
  (let [child (.resolve fs-root path)]
    (if (Files/exists child (into-array LinkOption []))
      child
      nil)))

(defn get-path [fs path]
  (.getPath fs path (into-array String [])))

(defn get-all-files [fs-root]
  (Files/newDirectoryStream fs-root))

(defn content [path]
  (apply str (Files/readAllLines path StandardCharsets/UTF_8)))

(defn default-file-system []
  (FileSystems/getDefault))
