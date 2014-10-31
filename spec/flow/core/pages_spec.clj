(ns flow.core.pages-spec
  (:require [speclj.core :refer :all]
            [flow.core.nio :as nio]
            [flow.core.pages :refer :all])
  (:import (com.google.common.jimfs Jimfs Configuration)))

(def ^:const sample-pages
  [{:path "test"
    :content "Title: Test\n\ncontent body"
    :published-at 2
    }])

(def test-fs
  (Jimfs/newFileSystem (Configuration/unix)))

(defn- create-file-system []
  (let [fs-root (nio/get-path test-fs "/work")]
    (doseq [page sample-pages]
      (let [file (nio/child-path fs-root (:path page))]
        (nio/create-file file (:content page) fs-root)
        (nio/set-last-modified file (:published-at page))))
    fs-root))


(defn- get-page [page-path]
  (let [fs-root (create-file-system)]
    (to-page (nio/child-path fs-root page-path) fs-root)))

(describe "to-page"
          (it "sets the title"
              (should= "Test" (:title (get-page "test")))))

