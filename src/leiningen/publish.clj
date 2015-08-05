(ns leiningen.publish
  (:require [pinaclj.tasks.publish :as task]
            [pinaclj.files :as files])
  (:import (java.time Instant)))

(defn- now []
  (Instant/now))

(def fs
  (files/init-default))

(defn ^{:no-project-needed true
        :help '[[file-path]]}
  publish [projects & args]
  (task/publish-path fs (first args) now))

