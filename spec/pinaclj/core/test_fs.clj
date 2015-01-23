(ns pinaclj.core.test-fs
  (:require [speclj.core :refer :all]
            [pinaclj.core.files :as files]) 
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
