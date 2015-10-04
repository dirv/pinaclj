(ns pinaclj.transforms.if-exists-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.if-exists :refer :all]))

(def page-with-value
  {:twitter "http://www.twitter.com/"})

(def page-with-no-key
  {})

(def page-with-empty-value
  {:instagram ""})

(def child-page-with-no-key
  {:parent "parent"})

(def all-pages
  {"parent" page-with-value})

(describe "if-exists"
  (it "returns delete when key doesn't exist"
    (should= {:delete nil} (if-exists page-with-no-key {:key "twitter"})))
  (it "returns empty when key exists"
    (should= {} (if-exists page-with-value {:key "twitter"})))
  (it "returns delete when key is empty"
    (should= {:delete nil} (if-exists page-with-empty-value {:key "instagram"})))
  (it "returns empty when key is on parent"
    (should= {} (if-exists child-page-with-no-key
                           {:key "twitter"
                            :all-pages all-pages}))))
