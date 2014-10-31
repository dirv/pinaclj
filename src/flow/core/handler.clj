(ns flow.core.handler
  (:require
    [flow.core.templates :as templates]
    [flow.core.nio :as nio]
    [ring.util.response :as response]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- sort-by-descending-date [pages]
  (reverse (sort-by :published-at pages)))

(defn- build-page-list [pages]
  (apply str (templates/page-list (sort-by-descending-date pages))))

(defn- not-found []
  (fn [req]
    (response/not-found "Not found")))

(defn- page-handler [app fs-root]
  (fn [req]
    (if-not (= :get (:request-method req))
      (app req)
      (let [path (subs (:uri req) 1)
            file (nio/existing-child-path fs-root path)]
        (if file
          {:status 200
           :body (nio/content file)
           :headers {"Content-Length" 0 ; todo
                     "Content-Type" 0}} ; todo
          (app req))))))

(defn- to-page [path fs-root]
  { :path (nio/get-path-string path fs-root)
   :content (nio/content path)
   :title "TEST" ; todo
   :published-at (nio/get-last-modified-time path)})

(defn- get-all-pages [fs-root]
  (with-open [children (nio/get-all-files fs-root)]
    (vec (map #(to-page % fs-root) children))))

(defn- index-handler [app fs-root]
  (fn [req]
    (if (and (= :get (:request-method req))
             (= "/" (:uri req)))
      {:status 200
       :body (build-page-list (get-all-pages fs-root))}
      (app req))))

(defn page-app [fs-root]
  (wrap-defaults (index-handler (page-handler (not-found) fs-root) fs-root) site-defaults))

(def app
  (page-app (nio/get-path (nio/default-file-system) "")))
