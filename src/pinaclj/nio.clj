(ns pinaclj.nio
  (:import (java.nio.file FileSystems Files LinkOption)
           (java.nio.file.attribute FileTime FileAttribute)
           (java.nio.file Files StandardOpenOption OpenOption)
           (java.nio.charset StandardCharsets)))

(defn get-path [fs path]
  (.getPath fs path (into-array String [])))

(defn resolve-path [fs-root path]
  (.resolve fs-root path))

(defn relativize [root-path path]
  (.relativize root-path path))

(defn directory-stream [path]
  (Files/newDirectoryStream path))

(defn input-stream [path]
  (Files/newInputStream path (into-array OpenOption [])))

(defn get-last-modified-time [path]
  (.toMillis (Files/getLastModifiedTime path (into-array LinkOption []))))

(defn set-last-modified [path millis]
  (Files/setLastModifiedTime path (FileTime/fromMillis millis)))

(defn read-all-lines [path]
  (Files/readAllLines path StandardCharsets/UTF_8))

(defn exists? [path]
  (Files/exists path (into-array LinkOption [])))

(defn directory? [path]
  (Files/isDirectory path (into-array LinkOption [])))

(defn hidden? [path]
  (Files/isHidden path))

(defn create-parent-directories [path]
  (let [parent (.getParent path)]
    (Files/createDirectories parent (into-array FileAttribute []))))

(defn create-file [path content]
  (Files/write path content (into-array OpenOption [])))

(defn default-file-system []
  (FileSystems/getDefault))
