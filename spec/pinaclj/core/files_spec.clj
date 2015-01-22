(ns pinaclj.core.files-spec
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.test-fs :as test-fs]
            [speclj.core :refer :all]))

(defn create-simple-nested-fs []
  (files/init (test-fs/test-fs) "/test")
  (files/create "nested/b.txt" "world")) 

(defn create-complex-nested-fs []
  (files/init (test-fs/test-fs) "/test")
  (files/create "a.txt" "hello")
  (files/create "nested/b.txt" "world") 
  (files/create "another/c.txt" "world")
  (files/create "yet/another/d.txt" "world"))

(defn create-empty-fs []
  (files/init (test-fs/test-fs) "/test"))

(defn- create-single-file-fs []
  (files/init (test-fs/test-fs) "/test") 
  (files/create "a.txt" "hello"))

(describe "all-in"
  (it "handles zero files"
    (create-empty-fs)
    (should= 0 (count (files/all-in "/"))))
  (it "handles one file"
    (create-single-file-fs)
    (should= 1 (count (files/all-in "/"))))
  (it "handles single nested file"
    (create-simple-nested-fs)
    (should= 1 (count (files/all-in "/"))))
  (it "handles complex nested file"
    (create-complex-nested-fs)
    (should= 4 (count (files/all-in "/")))));)
