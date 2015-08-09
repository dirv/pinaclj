(ns pinaclj.test-fs
  (:require [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.templates :as templates])
  (:import (com.google.common.jimfs Jimfs Configuration)))

(defn test-fs []
  (Jimfs/newFileSystem (Configuration/unix)))

(defn create-from [files]
  (let [fs  (files/init (test-fs) "/test")]
    (doseq [file files]
      (let [path (files/resolve-path fs (:path file))]
        (files/create path (:content file))
        (when (contains? file :modified)
          (nio/set-last-modified path (:modified file)))))
    fs))

(defn write-stream-file [fs [path stream]]
  (files/create (files/resolve-path fs (str "/test" path))
                (slurp stream)))

(defn create-empty []
  (create-from []))

(defn resource-stream [path]
  (.getResourceAsStream (clojure.lang.RT/baseLoader) path))

(defn file-exists? [fs file-path]
  (files/exists? (files/resolve-path fs file-path)))

(defn read-file [fs path]
  (clojure.string/join "\n" (nio/read-all-lines (nio/resolve-path fs path))))
