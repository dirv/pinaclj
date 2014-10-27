(ns flow.core.handler
  (:require [flow.core.templates :as templates]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- page-route [page]
  (GET (str "/" (:path page)) [] (apply str (templates/page page))))

(defn- sort-by-descending-date [pages]
  (sort #(> (:published-at %1) (:published-at %2)) pages))

(defn- build-page-list [pages]
  (apply str (templates/page-list (sort-by-descending-date pages))))

(defn- app-routes [pages]
  (apply routes
    (conj (conj
      (vec (map page-route pages))
      (GET "/" [] (build-page-list pages)))
      (route/not-found "Not Found"))))

(defn page-app [pages]
  (wrap-defaults (app-routes pages) site-defaults))

(def ^:const sample-pages
  [{:path "test" :content "content body" :published-at 1 :author "Daniel" :title "Test"}
   {:path "second" :content "second page" :published-at 2 :author "Daniel" :title "Second"}])

(def app
  (page-app sample-pages))
