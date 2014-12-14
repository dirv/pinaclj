(ns pinaclj.core.nio
  (:import (java.nio.file FileSystems Files LinkOption)
           (java.nio.file.attribute FileTime)
           (java.nio.file Files StandardOpenOption OpenOption)
           (java.nio.charset StandardCharsets)))

(defn get-path [fs path]
  (.getPath fs path (into-array String [])))

(defn relativize [fs-root path]
  (.toString (.relativize fs-root (.resolve fs-root path))))

(defn directory-stream [fs-root]
  (Files/newDirectoryStream fs-root))

(defn- get-last-modified-time [path]
  (Files/getLastModifiedTime path (into-array LinkOption [])))

(defn- set-last-modified [path millis]
  (Files/setLastModifiedTime path (FileTime/fromMillis millis)))

(defn read-all-lines [fs-root path]
  (Files/readAllLines (.resolve fs-root path) StandardCharsets/UTF_8))

(defn- file-exists? [fs-root path]
   (let [child (.resolve fs-root path)]
     (Files/exists child (into-array LinkOption []))))

(defn create-file [fs-root path content]
  (Files/write (.resolve fs-root path)
               content
               (into-array OpenOption [StandardOpenOption/CREATE])))

(defn default-file-system []
  (FileSystems/getDefault))
