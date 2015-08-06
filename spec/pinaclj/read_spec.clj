(ns pinaclj.read-spec
  (:require [speclj.core :refer :all]
            [pinaclj.read :refer :all]
            [pinaclj.date-time-helpers :as dt]
            [pinaclj.files :as files]
            [pinaclj.page-builder :as pb]
            [pinaclj.test-fs :as test-fs]))

(def published-at
  (dt/make 2014 10 31 10 5 0))

(def test-pages
  [{:path "first"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n---\ncontent body"
    :modified 1}

   {:path "second"
    :content "title: foo\nhello: World\n---\none\ntwo" }

   {:path "titleWithColon"
    :content "title: test: two\n"}

   {:path "titleWithNoValue"
    :content "title:\n"}

   {:path "longPage"
    :content (str "---\n" (apply str (repeat 200 "ab ")))}
   ])

(defn do-read [fs path-str]
  (read-page (pb/create-page fs (files/resolve-path fs path-str))))

(describe "read-page"
  (with fs (test-fs/create-from test-pages))

  (it "sets the path"
    (should= "/test/first" (.toString (:path (do-read @fs "first")))))

  (it "sets the title"
    (should= "Test" (:title (do-read @fs "first"))))

  (it "sets published-at"
    (should= published-at (:published-at (do-read @fs "first"))))

  (it "sets the raw content"
    (should= "content body" (:raw-content (do-read @fs "first"))))

  (it "sets content with multiple lines"
    (should= "one\ntwo" (:raw-content (do-read @fs "second"))))

  (it "does not set published-at for unpublished pages"
    (should= nil (:published-at (do-read @fs "second"))))

  (it "includes arbitrary headers"
    (should= "World" (:hello (do-read @fs "second"))))

  (it "parses title headers with colons"
    (should= "test: two" (:title (do-read @fs "titleWithColon"))))

  (it "parses headers with no value"
    (should-not (:title (do-read @fs "titleWithNoValue")))))

