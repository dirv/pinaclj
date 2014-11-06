(ns pinaclj.core.handler
  (:require
    [pinaclj.core.pages :as pages]
    [pinaclj.core.nio :as nio]
    [ring.util.response :as response :refer [response header]]
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
      (let [file (nio/child-path fs-root (file-path req))]
        (nio/create-file file (string-from-stream (:body req)))
        (response "")))))

(defn page-request? [fs-root req]
  (and (= :get (:request-method req))
       (nio/file-exists? fs-root (file-path req))))

(defn- page-handler [app fs-root]
  (fn [req]
    (if (page-request? fs-root req)
      (let [file (nio/child-path fs-root (file-path req))]
        (-> (response (nio/content file))
            (header "Content-Length" 0)
            (header "Content-Type" 0)))
      (app req))))

(defn- index-request? [req]
  (and (= :get (:request-method req))
       (= "/"  (:uri req))))

(defn- index-handler [app fs-root]
  (fn [req]
    (if (index-request? req)
      (response (pages/build-page-list fs-root))
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
