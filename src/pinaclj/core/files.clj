(ns pinaclj.core.files
  (:require [pinaclj.core.nio :as nio]))

(defn- as-bytes [st]
  (bytes (byte-array (map byte st))))

(def fs-root (atom ""))

(defn init [filesystem root]
  (reset! fs-root (nio/get-path filesystem root)))

(defn init-default []
  (init (nio/default-file-system) (System/getProperty "user.dir")))

(defn read-lines [path]
  (nio/read-all-lines @fs-root path))

(defn content [path]
  (clojure.string/join "\n" (read-lines path)))

(defn exists? [path]
  (nio/exists? @fs-root path))

(defn directory? [path]
  (nio/directory? @fs-root path))

(defn resolve-path [path-str]
  (nio/resolve-path @fs-root path-str))

(defn relativize [path]
  (nio/relativize @fs-root (resolve-path path)))

(defn create [path content]
  (nio/create-parent-directories @fs-root path)
  (nio/create-file @fs-root path (as-bytes content)))

(defn all-in [path]
  (with-open [file (nio/directory-stream @fs-root path)]
    (mapcat #(if (directory? %) (all-in %) [%]) file)))

