(ns pinaclj.test-templates
  (:require [pinaclj.test-fs :as test-fs]
            [pinaclj.templates :as templates]))

(defn- page-stream []
  (test-fs/resource-stream "example_theme/post.html"))

(defn- page-list-stream []
  (test-fs/resource-stream "example_theme/index.html"))

(defn- feed-stream []
  (test-fs/resource-stream "example_theme/feed.xml"))

(defn- func-params-stream []
  (test-fs/resource-stream "example_theme/func_params.html"))

(defn write-to-fs [fs]
  (doall (map #(test-fs/write-stream-file fs %) [["/theme/post.html" (page-stream)]
                                          ["/theme/index.html" (page-list-stream)]
                                           ["/theme/feed.xml" (feed-stream)]])))

(def page-list
  (templates/build-page-func (page-list-stream)))

(def feed-list
  (templates/build-page-func (feed-stream)))

(def page
  (templates/build-page-func (page-stream)))

(def func-params
  (templates/build-page-func (func-params-stream)))
