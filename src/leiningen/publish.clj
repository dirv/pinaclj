(ns leiningen.publish
  (:require [pinaclj.tasks.publish :as task]
            [pinaclj.files :as files]
            [pinaclj.date-time :as dt]
            [taoensso.tower :as tower]
            [leiningen.core.main :as main]))

(def fs
  (files/init-default))

(defn ^{:no-project-needed true
        :help '[[file-path]]}
  publish [projects & args]
  (doall (for [message (task/publish fs args dt/now)]
           (case (:type message)
                 :success (main/info (:msg message))
                 :error (main/warn (:msg message))
                 :fatal (main/abort (:msg message))))))
