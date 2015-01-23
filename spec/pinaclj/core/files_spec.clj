(ns pinaclj.core.files-spec
  (:require [pinaclj.core.files :as files]
            [pinaclj.core.test-fs :as test-fs]
            [speclj.core :refer :all]))

(defn create-simple-nested-fs []
  (files/init (test-fs/test-fs) "/test")
  (files/create (files/resolve-path "nested/b.txt") "world"))

(defn create-complex-nested-fs []
  (files/init (test-fs/test-fs) "/test")
  (files/create (files/resolve-path "a.txt") "hello")
  (files/create (files/resolve-path "nested/b.txt") "world") 
  (files/create (files/resolve-path "another/c.txt") "world")
  (files/create (files/resolve-path "yet/another/d.txt") "world"))

(defn create-empty-fs []
  (files/init (test-fs/test-fs) "/test"))

(defn- create-single-file-fs []
  (files/init (test-fs/test-fs) "/test") 
  (files/create (files/resolve-path "a.txt") "hello"))

(defn- file-count []
  (count (files/all-in (files/resolve-path "/"))))

(describe "all-in"
  (it "handles zero files"
    (create-empty-fs)
    (should= 0 (file-count)))
  (it "handles one file"
    (create-single-file-fs)
    (should= 1 (file-count)))
  (it "handles single nested file"
    (create-simple-nested-fs)
    (should= 1 (file-count)))
  (it "handles complex nested file"
    (create-complex-nested-fs)
    (should= 4 (file-count))))
