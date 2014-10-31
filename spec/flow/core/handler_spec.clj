(ns flow.core.handler-spec
  (:require [speclj.core :refer :all]
            [ring.mock.request :as mock]
            [flow.core.test-fs :as test-fs]
            [flow.core.handler :refer :all]))

(defn- create-sample-app []
  (page-app (test-fs/create-file-system)))

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
