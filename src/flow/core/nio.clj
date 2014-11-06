(ns flow.core.nio
  (:import (java.nio.file FileSystems Files LinkOption)
           (java.nio.file.attribute FileTime)
           (java.nio.file Files StandardOpenOption OpenOption)
           (java.nio.charset StandardCharsets)))

(defn child-path [fs-root file]
  (.resolve fs-root file))

(defn- as-bytes [st]
  (bytes (byte-array (map byte st))))

(defn get-path [fs path]
  (.getPath fs path (into-array String [])))

(defn get-path-string [fs-root path]
  (.toString (.relativize fs-root path)))

(defn get-all-files [fs-root]
  (Files/newDirectoryStream fs-root))

(defn get-last-modified-time [path]
  (Files/getLastModifiedTime path (into-array LinkOption [])))

(defn set-last-modified [path millis]
  (Files/setLastModifiedTime path (FileTime/fromMillis millis)))

(defn read-all-lines [path]
  (Files/readAllLines path StandardCharsets/UTF_8))

(defn content [path]
  (apply str (read-all-lines path)))

(defn file-exists? [fs-root path]
   (let [child (.resolve fs-root path)]
     (Files/exists child (into-array LinkOption []))))

(defn create-file [path content]
  (Files/write path
               (as-bytes content)
               (into-array OpenOption [StandardOpenOption/CREATE])))

(defn default-file-system []
  (FileSystems/getDefault))
