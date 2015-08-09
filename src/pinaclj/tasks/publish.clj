(ns pinaclj.tasks.publish
  (:require [pinaclj.read :as rd]
            [pinaclj.files :as files]
            [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]))

(def description
  "Publishes a page with the current timestamp.")

(defn- not-found-message [path]
  (str "The specified file " path " was not found."))

(defn- already-published-message [path]
  (str "The file " path " is already published."))

(defn- was-published-message [path time]
  (str "The file " path " has been published at " time "."))

(defn- read-page [src-root src-file]
  (rd/read-page (pb/create-page src-root src-file)))

(defn add-header [src-root page time-fn]
  (let [published-page (assoc page :published-at (time-fn))]
    (page/write-page published-page src-root)
    [(was-published-message (:path published-page)
                            (:published-at published-page))]))

(defn- publish-if-required [src-root page time-fn]
  (if (contains? page :published-at)
    [(already-published-message (:path page))]
    (add-header src-root page time-fn)))

(defn publish-path [src-root src-path time-fn]
  (let [path (files/resolve-path src-root src-path)]
    (if (files/exists? path)
      (publish-if-required src-root (read-page src-root path) time-fn)
      [(not-found-message src-path)])))
