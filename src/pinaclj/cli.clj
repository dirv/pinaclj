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

(defn- fs-stream [root-str page-str]
  (files/read-stream fs (str root-str page-str)))

(defn- template-func [theme-str]
  (templates/build-page-func (fs-stream theme-str "/templates/post.html")))

(defn- index-func [theme-str]
  (templates/build-list-func (fs-stream theme-str "/single/index.html")))

(defn- run-compile [{src :source dest :destination theme :theme}]
  (core/compile-all (nio/resolve-path fs src)
                    (nio/resolve-path fs dest)
                    (template-func theme)
                    (index-func theme)))

(defn main [args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (cond
     (:help options)  (println (usage summary))
      :else           (run-compile options))))

(defn -main [& args]
  (main args))
