(ns pinaclj.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [pinaclj.core :as core]
            [pinaclj.files :as files]
            [pinaclj.nio :as nio]
            [pinaclj.templates :as templates])
  (:gen-class))

(def fs
  (files/init-default))

(def cli-options
  [["-s" "--source SOURCE          " "Source directory.     " :default "pages"]
   ["-d" "--destination DESTINATION" "Destination directory." :default "generated"]
   ["-t" "--theme THEME"             "Theme directory."       :default "theme"]
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

(defn- template-path [theme-str]
  (nio/resolve-path fs (str theme-str "/templates/post.html")))

(defn- index-path [theme-str]
  (nio/resolve-path fs (str theme-str "/single/index.html")))

(defn- template-func [{theme-str :theme}]
  (templates/build-page-func (nio/input-stream (template-path theme-str))))

(defn- index-func [{theme-str :theme}]
  (templates/build-list-func (nio/input-stream (index-path theme-str))))

(defn- run-compile [opts]
  (let [fs          (files/init-default)
        source      (files/resolve-path fs (:source opts))
        destination (files/resolve-path fs (:destination opts))]
    (core/compile-all source destination (template-func opts) (index-func opts))))

(defn main [args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (cond
     (:help options)  (println (usage summary))
      :else           (run-compile options))))

(defn -main [& args]
  (main args))
