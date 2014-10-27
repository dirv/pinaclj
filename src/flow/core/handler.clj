(ns flow.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- page-route [page]
  (GET (str "/" (:path page)) [] (:content page)))

(defn- build-page-link [page]
  (str "<a href=\"/" (:path page) "\">" (:path page) "</a><br></br>"))

(defn- sort-by-descending-date [pages]
  (sort #(> (:published-at %1) (:published-at %2)) pages))

(defn- build-page-list [pages]
  (clojure.string/join "\n" (map build-page-link (sort-by-descending-date pages))))

(defn- app-routes [pages]
  (apply routes
    (conj (conj
      (vec (map page-route pages))
      (GET "/" [] (build-page-list pages)))
      (route/not-found "Not Found"))))

(defn page-app [pages]
  (wrap-defaults (app-routes pages) site-defaults))

(def app
  (page-app []))
