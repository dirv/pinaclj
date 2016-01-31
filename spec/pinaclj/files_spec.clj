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

(def duplicate-files
  [{:path "from/sub/file.txt"}
   {:path "to/existing.txt"}])

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

(describe :duplicate-if-newer
  (with fs (test-fs/create-from duplicate-files))

  (it "creates non-existing directories"
    (let [src (files/resolve-path @fs "from")
          dest (files/resolve-path @fs "to")
          file (files/resolve-path @fs "from/sub/file.txt")]
      (files/duplicate-if-newer src dest file)
      (should (files/exists? (files/resolve-path @fs "to/sub/file.txt"))))))

