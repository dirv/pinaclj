(ns pinaclj.transforms.modified-spec
  (:require [speclj.core :refer :all]
            [pinaclj.page-builder :as pb]
            [pinaclj.files :as files]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.transforms.modified :refer :all]))

(def page-one {:modified 1 :src-modified 1 :path "a.md"})

(def page-two {:modified 2 :src-modified 2 :path "b.md"})

(def opts {:all-pages {"a" page-one
                       "b" page-two}})

(def grandparent-page {:src-modified 3})
(def parent-page {:src-modified 1 :parent "gp"})
(def child-page {:src-modified 1 :parent "p"})

(def hierarchy-opts {:all-pages {"c" child-page
                                 "p" parent-page
                                 "gp" grandparent-page}})
(defn do-read [fs path-str]
  (pb/create-page fs (files/resolve-path fs path-str)))

(def multi-page
  {:pages ["a" "b"]})

(def test-pages
  [page-one page-two])

(describe "find-modified"
  (with fs (test-fs/create-from test-pages))

  (it "uses modified date for simple page"
    (should= 1 (find-modified page-one opts)))

  (it "uses latest modified date for page list"
    (should= 2 (find-modified multi-page opts)))

  (it "uses parent latest if parent page has been modified"
    (should= 3 (find-modified child-page hierarchy-opts))))

