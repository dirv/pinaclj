(ns flow.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- page-route [page]
  (GET (str "/" page) [] "Hello World"))

(defn- app-routes [pages]
  (apply routes
    (conj (conj
      (vec (map page-route pages))
      (GET "/" [] "Hello World"))
      (route/not-found "Not Found"))))

(defn page-app [pages]
  (wrap-defaults (app-routes pages) site-defaults))

(def app
  (page-app []))
