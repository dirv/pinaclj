(ns pinaclj.test-templates
  (:require [pinaclj.test-fs :as test-fs]
            [pinaclj.templates :as templates]))

(defn stream [page]
  (test-fs/resource-stream (str "example_theme/" page)))

(defn- write-file [fs [from to]]
  (test-fs/write-stream-file fs [(str "/theme/" to) (stream from)]))

(defn write-to-fs [fs]
  (doall (map (partial write-file fs) [["post.html" "post.html"]
                                       ["index.html" "index.html"]
                                       ["feed.xml" "feed.xml"]])))

(defn write-split-to-fs [fs]
  (doall (map (partial write-file fs) [["post.html" "post.html"]
                                       ["split_list.html" "index.html"]
                                       ["feed.xml" "feed.xml"]])))
