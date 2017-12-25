(ns pinaclj.cli
  (:require [pinaclj.tasks.publish :as publish]
            [pinaclj.tasks.generate :as generate]
            [clojure.tools.cli :refer [parse-opts]]
            [pinaclj.date-time :as dt]
            [pinaclj.files :as files])
  (:gen-class))

(def fs
  (files/init-default))

(defn- usage [options-summary]
    [""
     "pinaclj is a command-line tool for generating a static website from markdown files."
     ""
     "Usage: lein generate [options]"
     ""
     "Options:"
     "  switch                         default    description"
     options-summary
     ""
     "lein publish file1 file2..."
     ""])

(def cli-options
  [["-s" "--source SOURCE          " "Source directory.     " :default "pages"]
   ["-d" "--destination DESTINATION" "Destination directory." :default "generated"]
   ["-t" "--theme THEME"             "Theme directory."       :default "theme"]
   ["-h" "--help" "Print help."]])

(defn print-error [msg]
  (binding [*out* *err*]
    (println msg)))

(defn print-message [message]
  (case (:type message)
    :success (println (:msg message))
    :info (println (:msg message))
    :error (print-error (:msg message))))

(defn- run-generate [{src :source dest :destination theme :theme}]
  (generate/generate fs src dest theme))

(defn run [& args]
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)]
    (case (first arguments)
      "publish" (doall (map print-message (publish/publish fs (rest arguments) dt/now)))
      "generate" (doall (map print-message (run-generate options)))
      (doall (map println (usage summary))))))
