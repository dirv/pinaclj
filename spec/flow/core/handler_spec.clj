(ns flow.core.handler-spec
  (:require [speclj.core :refer :all]
            [ring.mock.request :as mock]
            [flow.core.handler :refer :all]))

(describe "main route"
          (it "responds"
              (should= 200 (:status (app (mock/request :get "/")))))
          (it "has a body"
              (should= "Hello World" (:body (app (mock/request :get "/"))))))

(describe "not-found route"
          (it "responds with 404"
              (should= 404 (:status (app (mock/request :get "/invalid"))))))

(describe "simple page route"
          (it "responds with 200"
              (should= 200 (:status ((page-app ["test"]) (mock/request :get "/test"))))))
