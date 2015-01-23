(ns pinaclj.core.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [pinaclj.core.compile :as cmp]
            [pinaclj.core.files :as files]
            [pinaclj.core.templates :as templates])
  (:gen-class))

(def cli-options
  [["-s" "--source SOURCE          " "Source directory.     " :default "drafts"]
   ["-d" "--destination DESTINATION" "Destination directory." :default "published"]
   ["-h" "--help" "Print help."]])

(defn- usage [options-summary]
  (->> [""
        "pinaclj-core is a command-line tool for generating a static website from markdown files."
        ""
        "Usage: lein run [options]"
        ""
        "Options:"
        "  switch                         default    description"
        options-summary
        ""]
       (string/join \newline)))

(defn- run-compile [opts]
  (let [fs          (files/init-default)
        source      (files/resolve-path fs (:source opts))
        destination (files/resolve-path fs (:destination opts))]
    (cmp/compile-all source destination templates/page)))

(defn main [args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (cond
     (:help options)  (println (usage summary))
      :else           (run-compile options))) )

(defn -main [& args]
  (main args))
