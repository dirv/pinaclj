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

(def page-link
  (templates/build-link-func (page-list-stream)))

(def feed-link
  (templates/build-link-func (feed-stream)))

(def page-list
  (templates/build-list-func (page-list-stream) page-link))

(def feed-list
  (templates/build-list-func (feed-stream) feed-link))

(def page
  (templates/build-page-func (page-stream)))

(def func-params
  (templates/build-page-func (func-params-stream)))
