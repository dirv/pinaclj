(ns pinaclj.files
  (:require [pinaclj.nio :as nio]))

(defn- as-bytes [st]
  (bytes (byte-array (map byte st))))

(defn init [filesystem root]
  (nio/get-path filesystem root))

(defn init-default []
  (init (nio/default-file-system) (System/getProperty "user.dir")))

(defn read-lines [path]
  (nio/read-all-lines path))

(defn content [path]
  (clojure.string/join "\n" (read-lines path)))

(defn exists? [path]
  (nio/exists? path))

(defn directory? [path]
  (nio/directory? path))

(defn resolve-path [fs-root path-str]
  (nio/resolve-path fs-root path-str))

(defn read-stream [fs path]
  (nio/input-stream (nio/resolve-path fs path)))

(defn create [path content]
  (nio/create-parent-directories path)
  (nio/create-file path (.getBytes content)))

(defn all-in [path]
  (with-open [files (nio/directory-stream path)]
    (doall (mapcat #(if (directory? %) (all-in %) [%]) files))))

(defn remove-extension [path]
  (let [path-str (str path)]
    (subs path-str 0 (.lastIndexOf path-str "."))))

(defn change-extension-to-html [path]
  (str (remove-extension path) ".html"))

(defn trim-url [url-str]
  (if (= \/ (first url-str))
    (subs url-str 1)
    url-str))
