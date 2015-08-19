(ns leiningen.generate
  (:require [pinaclj.tasks.generate :as task]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [pinaclj.files :as files]
            [leiningen.core.main :as main]))

(def fs
  (files/init-default))

(defn- usage [options-summary]
  (->> [""
        "pinaclj is a command-line tool for generating a static website from markdown files."
        ""
        "Usage: lein run [options]"
        ""
        "Options:"
        "  switch                         default    description"
        options-summary
        ""]
       (string/join \newline)))

(def cli-options
  [["-s" "--source SOURCE          " "Source directory.     " :default "pages"]
   ["-d" "--destination DESTINATION" "Destination directory." :default "generated"]
   ["-t" "--theme THEME"             "Theme directory."       :default "theme"]
   ["-h" "--help" "Print help."]])

(defn- run-generate [{src :source dest :destination theme :theme}]
  (task/generate fs src dest theme))

(defn ^{:no-project-needed true
        :help '[[file-path]]}
  generate [projects & args]

  (let [{:keys [options options-summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (main/info (usage options-summary))
      :else
      (doall (for [message (run-generate options)]
               (case (:type message)
                 :success (main/info (:msg message))
                 :error (main/warn (:msg message))))))))
