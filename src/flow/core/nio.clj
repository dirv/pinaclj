(ns flow.core.nio
  (:import (java.nio.file FileSystems Files LinkOption)
           (java.nio.file.attribute FileTime)
           (java.nio.file Files StandardOpenOption OpenOption)
           (java.nio.charset StandardCharsets)))

(defn child-path [fs-root file]
  (.resolve fs-root file))

(defn- as-bytes [st]
  (bytes (byte-array (map byte st))))

(defn existing-child-path [fs-root path]
  (let [child (.resolve fs-root path)]
    (if (Files/exists child (into-array LinkOption []))
      child
      nil)))

(defn get-path [fs path]
  (.getPath fs path (into-array String [])))

(defn get-path-string [path fs-root]
  (.toString (.relativize fs-root path)))

(defn get-all-files [fs-root]
  (Files/newDirectoryStream fs-root))

(defn get-last-modified-time [path]
  (Files/getLastModifiedTime path (into-array LinkOption [])))

(defn set-last-modified [path millis]
  (Files/setLastModifiedTime path (FileTime/fromMillis millis)))

(defn content [path]
  (apply str (Files/readAllLines path StandardCharsets/UTF_8)))

(defn create-file [path content directory]
  (Files/write path
               (as-bytes content)
               (into-array OpenOption [StandardOpenOption/CREATE])))

(defn default-file-system []
  (FileSystems/getDefault))
