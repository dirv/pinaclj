(ns pinaclj.tasks.publish
  (:require [pinaclj.read :as rd]
            [pinaclj.files :as files]
            [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]
            [taoensso.tower :as tower]
            [pinaclj.tasks.task :as task]
            [pinaclj.translate :refer :all]))

(def description
  "Publishes a page with the current timestamp.")

(defn- read-page [src-root src-file]
  (rd/read-page (pb/create-page src-root src-file)))

(defn add-header [src-root page time-fn]
  (let [published-page (assoc page :published-at (time-fn))]
    (page/write-page published-page src-root)
    (task/success (t :en :was-published
                     (:path published-page)
                     (:published-at published-page)))))

(defn- publish-if-required [src-root page time-fn]
  (if (contains? page :published-at)
    (task/error (t :en :already-published (:path page)))
    (add-header src-root page time-fn)))

(defn- publish-path [src-root src-path time-fn]
  (let [path (files/resolve-path src-root src-path)]
    (if (files/exists? path)
      (publish-if-required src-root (read-page src-root path) time-fn)
      (task/error (t :en :not-found src-path)))))

(defn publish [src-root src-paths time-fn]
  (tower/with-tscope :publish
    (vec (map #(publish-path src-root % time-fn) src-paths))))
