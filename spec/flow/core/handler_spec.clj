(ns flow.core.handler-spec
  (:require [speclj.core :refer :all]
            [ring.mock.request :as mock]
            [flow.core.nio :as nio]
            [flow.core.handler :refer :all])
  (:import (com.google.common.jimfs Jimfs Configuration)))

(def ^:const sample-pages
  [{:path "test" :content "Title: title\n\ncontent body" :published-at 2 :author "Daniel" :title "Test"}
   {:path "second" :content "Title: foo\n\nsecond page" :published-at 1 :author "Daniel" :title "Second"}])

(def test-fs
  (Jimfs/newFileSystem (Configuration/unix)))

(defn- create-file-system []
  (let [fs-root (nio/get-path test-fs "/work")]
    (doseq [page sample-pages]
      (let [file (nio/child-path fs-root (:path page))]
        (nio/create-file file (:content page) fs-root)
        (nio/set-last-modified file (:published-at page))))
    fs-root))

(defn- create-sample-app []
  (page-app (create-file-system)))

(defn- get-request [path]
  ((create-sample-app) (mock/request :get path)))

(describe "main route"
          (it "responds"
              (should= 200 (:status (get-request "/"))))
          (it "has a body"
              (should-contain "href=\"/test\"" (:body (get-request "/")))))

(describe "not-found route"
          (it "responds with 404"
              (should= 404 (:status (get-request "/invalid")))))

(describe "simple page route"
          (it "responds with 200"
              (should= 200 (:status (get-request "/test"))))
          (it "displays content"
              (should-contain "content body" (:body (get-request "/test")))))

(describe "page list"
          (it "orders by descending date"
              (should-contain #"test(?s).*second" (:body (get-request "/")))))
