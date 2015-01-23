(ns pinaclj.core.files-spec
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.test-fs :as test-fs]
            [speclj.core :refer :all]))

(def single-file
  [{:path "a.txt"
    :content ""}])

(def simple-nested
  [{:path "nested/b.txt"
    :content ""}])

(def complex-nested
  [{:path "a.txt"
    :content ""}
   {:path "nested/b.txt"
    :content ""}
   {:path "another/c.txt"
    :content ""}
   {:path "yet/another/d.txt"
    :content ""}])

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
    (should= 4 (file-count (test-fs/create-from complex-nested)))))
