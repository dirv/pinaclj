(ns pinaclj.core.nio
  (:import (java.nio.file FileSystems Files LinkOption)
           (java.nio.file.attribute FileTime FileAttribute)
           (java.nio.file Files StandardOpenOption OpenOption)
           (java.nio.charset StandardCharsets)))

(defn get-path [fs path]
  (.getPath fs path (into-array String [])))

(defn relativize [fs-root path]
  (.toString (.relativize fs-root (.resolve fs-root path))))

(defn directory-stream [fs-root path]
  (Files/newDirectoryStream (.resolve fs-root path)))

(defn- get-last-modified-time [path]
  (Files/getLastModifiedTime path (into-array LinkOption [])))

(defn- set-last-modified [path millis]
  (Files/setLastModifiedTime path (FileTime/fromMillis millis)))

(defn read-all-lines [fs-root path]
  (Files/readAllLines (.resolve fs-root path) StandardCharsets/UTF_8))

(defn exists? [fs-root path]
  (Files/exists (.resolve fs-root path) 
                (into-array LinkOption [])))

(defn directory? [fs-root path]
  (Files/isDirectory (.resolve fs-root path)
                     (into-array LinkOption [])))

(defn create-parent-directories [fs-root path]
  (let [parent (.getParent (.resolve fs-root path))]
    (Files/createDirectories parent (into-array FileAttribute []))))

(defn create-file [fs-root path content]
  (Files/write (.resolve fs-root path)
               content
               (into-array OpenOption [StandardOpenOption/CREATE])))

(defn default-file-system []
  (FileSystems/getDefault))
