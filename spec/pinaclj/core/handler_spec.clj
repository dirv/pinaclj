(ns pinaclj.core.handler-spec
  (:require [speclj.core :refer :all]
            [clojure.pprint]
            [peridot.core :as peridot]
            [ring.middleware.anti-forgery :as af]
            [pinaclj.core.test-fs :as test-fs]
            [pinaclj.core.handler :refer :all]))

(defn- create-sample-app []
  (page-app (test-fs/create-file-system)))

(defn- get-request [path]
  (-> (create-sample-app)
    (peridot/session)
    (peridot/request path)
    (:response)))

(defn- post-then-get-request [path content]
  (-> (create-sample-app)
    (peridot/session)
    (peridot/request path
                     :request-method :post
                     :content-type "application/json"
                     :body content)
    (peridot/request path)
    (:response)))

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

(describe "writing a page"
  (it "writes and read back a page"
    (should-contain "hello world" (:body (post-then-get-request "/newpage" "{\"entry\": {\"content\": \"hello world\"}}"))))

  (it "writes and read back a page with headers"
    (should= "title: hello\n\nhello world" (:body (post-then-get-request "/newpage" "{\"entry\": {\"title\": \"hello\", \"content\": \"hello world\"}}")))))
