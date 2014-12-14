(ns pinaclj.core.test-fs
  (:require [speclj.core :refer :all]
            [pinaclj.core.files :as files]) 
  (:import (com.google.common.jimfs Jimfs Configuration)))

(def ^:const sample-pages
  [{:path "test"
    :content "title: Test\nhello: World\npublished-at: 2014-10-31T10:05:00Z\n\ncontent body"
    }
   {:path "second"
    :content "title: foo\npublished-at: 2014-10-30T09:00:00Z\n\none\ntwo"
    }
   {:path "unpublished"
    :content "title: Unpublished\n\ncontent"}
   {:path "coffee ritual"
    :content "title: The Coffee Ritual\n\nyellow cup green cup"}])

(defn test-fs []
  (Jimfs/newFileSystem (Configuration/unix)))

(defn create-file-system []
  (files/init (test-fs) "/work")
  (doseq [page sample-pages]
    (files/create (:path page) (:content page))))
