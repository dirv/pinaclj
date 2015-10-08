(ns leiningen.make-sample
  (:import (java.time Instant ZoneId ZonedDateTime LocalDateTime))
  (:require [pinaclj.nio :as nio]
            [pinaclj.files :as files]
            [leiningen.sample-data.last-names :as last-names]
            [leiningen.sample-data.first-names :as first-names]
            [clojure.tools.cli :refer [parse-opts]]
            [markov.core :as markov]
            [pinaclj.date-time :as dt]))


(def alice (markov/build-from-string (slurp "src/leiningen/sample_data/alice.txt")))

(defn- random-timestamp []
  (.minusSeconds (dt/now) (rand-int 100000000)))

(defn- to-id [title]
  (apply str (take 40 (-> title
                          (clojure.string/replace #"[^a-zA-Z\ 0-9]+" "")
                          (clojure.string/replace \ \-)
                          (clojure.string/lower-case)))))

(defn- random-name []
  (str (rand-nth first-names/first-names) " " (rand-nth last-names/last-names)))

(defn- build-author-page []
  (let [author (random-name)]
    [(to-id author) (str "title: " author "\n"
                         "published-at: " (random-timestamp) "\n"
                         "category: author\n---\n")]))

(def fs
  (nio/default-file-system))

(defn- write-page [dir page]
  (nio/create-file (nio/resolve-path dir (str (first page) ".md"))
                   (.getBytes (second page))))

(defn- create-authors []
  (let [author-dir (nio/get-path fs "team/authors")]
    (nio/create-parent-directories author-dir)
    (doall (map (partial write-page author-dir)
                (repeatedly 100 (build-author-page))))))

(defn capitalize-words [s]
  (->>  (clojure.string/split  (str s) #"\b")
       (map clojure.string/capitalize)
       (clojure.string/join)))

(defn- to-words [id]
  (-> id
      (clojure.string/replace \- \ )
      (capitalize-words)))

(defn- convert-author-filename [path]
  (to-words (files/remove-extension (.getFileName path))))

(defn- find-authors []
  (let [author-dir (nio/get-path fs "team/authors")]
    (map convert-author-filename (files/all-in author-dir))))

(def all-tags ["Craftsmanship" "TDD" "C#" "Java" "Clojure" "Waza" "Writing"
               "London" "Chicago" "Alice" "Perl" "Ruby" "Python" "Apprenticeship"])

(defn- build-tags []
  (apply str (interpose ", " (distinct (repeatedly (inc (rand-int 4)) #(rand-nth all-tags))))))

(defn- build-sentence []
  (apply str (interpose " " (take (+ 5 (rand-int 10)) (markov/generate-walk alice)))))

(defn- build-post []
  (apply str (interpose " " (take 1000 (markov/generate-walk alice)))))

(defn- create-page [author]
  (let [title (build-sentence)]
    [(to-id title)
     (str "title: " title "\n"
          "author: " author "\n"
          "published-at: " (random-timestamp) "\n"
          "tags: " (build-tags) "\n"
          "---\n"
          (build-post))]))

(defn- create-pages []
  (let [authors (find-authors)
        posts-dir (nio/get-path fs "team/posts")]
    (nio/create-parent-directories posts-dir)
    ;(println (create-page (rand-nth authors)))
    (doall (map (partial write-page posts-dir) (repeatedly 50 #(create-page (rand-nth authors)))))
    ))

(defn ^{:no-project-needed true}
  make-sample [projects & args]
  (create-pages))

