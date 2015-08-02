(ns pinaclj.transforms.modified-spec
  (:require [speclj.core :refer :all]
            [pinaclj.page-builder :as pb]
            [pinaclj.files :as files]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.transforms.modified :refer :all]))

(def page-one
  {:modified 1
   :path "a"
   :content ""})

(def page-two
  {:modified 2
   :path "b"
   :content ""})

(defn do-read [fs path-str]
  (pb/create-page fs (files/resolve-path fs path-str)))

(defn- multi-page [fs]
  {:pages [(do-read fs "a") (do-read fs "b")]})

(def test-pages
  [page-one page-two])

(describe "find-modified"
  (with fs (test-fs/create-from test-pages))

  (it "uses modified date for simple page"
    (should= 1 (find-modified (do-read @fs "a") {})))

  (it "uses latest modified date for page list"
    (should= 2 (find-modified (multi-page @fs) {}))))

