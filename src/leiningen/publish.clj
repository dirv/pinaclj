(ns leiningen.publish
  (:require [pinaclj.tasks.publish :as task]
            [pinaclj.files :as files]
            [pinaclj.date-time :as dt]))

(def fs
  (files/init-default))

(defn ^{:no-project-needed true
        :help '[[file-path]]}
  publish [projects & args]
  (let [message (task/publish-path fs (first args) dt/now)]
    (println (last message))))

