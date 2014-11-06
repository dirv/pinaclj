(ns pinaclj.core.test-fs
  (:require [speclj.core :refer :all]
            [pinaclj.core.nio :as nio])
  (:import (com.google.common.jimfs Jimfs Configuration)))

(def ^:const sample-pages
  [{:path "test"
    :content "title: Test\nhello: World\npublished-at: 2014-10-31T10:05:00Z\n\ncontent body"
    }
   {:path "second"
    :content "title: foo\npublished-at: 2014-10-30T09:00:00Z\n\none\ntwo"
    }
   {:path "unpublished"
    :content "title: Unpublished\n\ncontent"}])

(def test-fs
  (Jimfs/newFileSystem (Configuration/unix)))

(defn create-file-system []
  (let [fs-root (nio/get-path test-fs "/work")]
    (doseq [page sample-pages]
      (let [file (nio/child-path fs-root (:path page))]
        (nio/create-file file (:content page))))
    fs-root))
