(ns pinaclj.core.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [pinaclj.core.pages.compile :as cmp]
            [pinaclj.core.files :as files]
            [pinaclj.core.templates :as templates])
  (:gen-class))

(def cli-options
  [["-s" "--source" "Source directory." :default "drafts"]
   ["-d" "--destination" "Destination directory." :default "published"]
   ["-h" "--help"]])

(defn- usage [options-summary]
  (->> [""
        "pinaclj-core is a command-line tool for generating a static website from markdown files."
        ""
        "Usage: lein run [options]"
        ""
        "Options[switch, description]:"
        options-summary
        ""]
       (string/join \newline)))

(defn- exit [status msg]
  (println msg)
  (System/exit status))

(defn- run-compile [opts]
  (files/init-default)
  (cmp/run (:source opts) (:destination opts) templates/page))

(defn -main [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (cond
     (:help options)  (exit 0 (usage summary))
      :else           (run-compile options))))
