(ns flow.core.handler-spec
  (:require [speclj.core :refer :all]
            [ring.mock.request :as mock]
            [flow.core.nio :as nio]
            [flow.core.handler :refer :all])
  (:import (com.google.common.jimfs Jimfs Configuration)
           (java.nio.file Files StandardOpenOption OpenOption)))

(def ^:const sample-pages
  [{:path "test" :content "content body" :published-at 1 :author "Daniel" :title "Test"}
   {:path "second" :content "second page" :published-at 2 :author "Daniel" :title "Second"}])

(defn- as-bytes [st]
  (bytes (byte-array (map byte st))))

(defn- file-path [file fs]
  (.getPath fs (:path file) (into-array String [])))

(def open-options
  (into-array OpenOption [StandardOpenOption/CREATE]))

(defn- create-file [file fs]
  (Files/write (file-path file fs) (as-bytes (:content file)) open-options))

(defn- create-file-system [files]
  (let [fs (Jimfs/newFileSystem (Configuration/unix))]
    (doseq [file files] (create-file file fs))
    fs))

(defn- test-fs []
  (create-file-system sample-pages))

(defn- create-sample-app []
  (page-app (nio/get-path (test-fs) "")))

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
          (xit "orders by descending date"
              (should-contain #"second(?s).*test" (:body (get-request "/")))))
