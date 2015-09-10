(ns pinaclj.files-spec
  (:require [pinaclj.files :as files]
            [pinaclj.test-fs :as test-fs]
            [speclj.core :refer :all]))

(def single-file
  [{:path "a.txt"}])

(def simple-nested
  [{:path "nested/b.txt"}])

(def complex-nested
  [{:path "a.txt"}
   {:path "nested/b.txt"}
   {:path "another/c.txt"}
   {:path "yet/another/d.txt"}])

(def hidden-file
  [{:path ".hidden"}])

(defn- file-count [fs]
  (count (files/all-in (files/resolve-path fs "/"))))

(describe "all-in"
  (it "handles zero files"
    (should= 0 (file-count (test-fs/create-from []))))

  (it "handles one file"
    (should= 1 (file-count (test-fs/create-from single-file))))

  (it "handles single nested file"
    (should= 1 (file-count (test-fs/create-from simple-nested))))

  (it "handles complex nested file"
    (should= 4 (file-count (test-fs/create-from complex-nested))))

  (it "does not include hidden files"
    (should= 0 (file-count (test-fs/create-from hidden-file)))))
