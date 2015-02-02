(ns pinaclj.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [pinaclj.core :as core]
            [pinaclj.files :as files]
            [pinaclj.templates :as templates])
  (:gen-class))

(def cli-options
  [["-s" "--source SOURCE          " "Source directory.     " :default "pages"]
   ["-d" "--destination DESTINATION" "Destination directory." :default "generated"]
   ["-t" "--theme"                   "Theme directory."       :default "theme"]
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
        destination (files/resolve-path fs (:destination opts))
        theme-str   (:theme opts)]
    (core/compile-all source destination templates/page)))

(defn main [args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (cond
     (:help options)  (println (usage summary))
      :else           (run-compile options))))

(defn -main [& args]
  (main args))
