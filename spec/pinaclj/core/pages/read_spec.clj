(ns pinaclj.core.pages.read-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.pages.read :refer :all]
            [pinaclj.core.pages.date-time :as date-time]
            [pinaclj.core.files :as files]
            [pinaclj.core.test-fs :as test-fs]))

(def published-at
  (date-time/make 2014 10 31 10 5 0))

(def test-pages
  [{:path "first"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n\ncontent body"
    }
   {:path "second"
    :content "title: foo\nhello: World\n\none\ntwo" }])

(defn do-read [path-str]
  (read-page (files/resolve-path path-str)))

(describe "read-page"
  (before (test-fs/create-from test-pages))

  (it "sets the title"
    (should= "Test" (:title (do-read "first"))))

  (it "sets published-at"
    (should= published-at (:published-at (do-read "first"))))

  (it "sets the content"
    (should= "content body" (:content (do-read "first"))))

  (it "sets content with multiple lines"
    (should= "one\ntwo" (:content (do-read "second"))))

  (it "does not set published-at for unpublished pages"
    (should= nil (:published-at (do-read "second"))))

  (it "includes arbitrary headers"
    (should= "World" (:hello (do-read "second")))))
