(ns pinaclj.core.test-fs
  (:require [speclj.core :refer :all]
            [pinaclj.core.files :as files]) 
  (:import (com.google.common.jimfs Jimfs Configuration)))

(defn test-fs []
  (Jimfs/newFileSystem (Configuration/unix)))

(defn create-from [pages]
  (files/init (test-fs) "/test")
  (doseq [page pages]
    (files/create (files/resolve-path (:path page)) (:content page))))

(defn create-empty []
  (create-from []))
