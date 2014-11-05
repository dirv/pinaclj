(ns flow.core.handler
  (:require
    [flow.core.pages :as pages]
    [flow.core.nio :as nio]
    [ring.util.response :as response]
    [ring.util.anti-forgery :as af]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(defn- not-found [req]
  (response/not-found "Not found"))

(defn- string-from-stream [stream]
  (.reset stream)
  (slurp stream))

(defn- file-path [req]
  (subs (:uri req) 1))

(defn- post-handler [app fs-root]
  (fn [req]
    (if-not (= :post (:request-method req))
      (app req)
      (let [path (file-path req)
            file (nio/child-path fs-root path)]
        (nio/create-file file (string-from-stream (:body req)))
        {:status 200}))))

(defn- find-file [fs-root req]
  (nio/existing-child-path fs-root (file-path req)))

(defn- page-handler [app fs-root]
  (fn [req]
    (if-not (= :get (:request-method req))
      (app req)
      (if-let [file (find-file fs-root req)]
        {:status 200
         :body (nio/content file)
         :headers {"Content-Length" 0 ; todo
                   "Content-Type" 0 }}
        (app req)))))

(defn- index-request? [req]
  (and (= :get (:request-method req))
       (= "/"  (:uri req))))

(defn- index-handler [app fs-root]
  (fn [req]
    (if (index-request? req)
      {:status 200
       :body (pages/build-page-list fs-root)}
      (app req))))

(defn page-app [fs-root]
  (-> not-found
    (page-handler fs-root)
    (index-handler fs-root)
    (post-handler fs-root)
    (wrap-defaults api-defaults)))

(def app
  (-> (nio/default-file-system)
    (nio/get-path "")
    page-app))
