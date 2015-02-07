(ns pinaclj.test-templates
  (:require [pinaclj.test-fs :as test-fs]
            [pinaclj.templates :as templates]))

(defn- page-stream []
  (test-fs/resource-stream "templates/page.html"))

(defn- page-list-stream []
  (test-fs/resource-stream "templates/page_list.html"))

(def page-link
  (templates/build-link-func (page-list-stream)))

(def page-list
  (templates/build-list-func (page-list-stream) page-link))

(def page
  (templates/build-page-func (page-stream)))

