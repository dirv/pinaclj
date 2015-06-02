(ns pinaclj.read-spec
  (:require [speclj.core :refer :all]
            [pinaclj.read :refer :all]
            [pinaclj.date-time :as date-time]
            [pinaclj.files :as files]
            [pinaclj.page :as page]
            [pinaclj.test-fs :as test-fs]))

(def published-at
  (date-time/make 2014 10 31 10 5 0))

(def test-pages
  [{:path "first"
    :content "title: Test\npublished-at: 2014-10-31T10:05:00Z\n---\ncontent body"}
   {:path "second"
    :content "title: foo\nhello: World\n---\none\ntwo" }

   {:path "titleWithColon"
    :content "title: test: two\n"}

   {:path "titleWithNoValue"
    :content "title:\n"}

   {:path "longPage"
    :content (str "---\n" (apply str (repeat 200 "ab ")))}
   ]
  )

(defn do-read [fs path-str]
  (read-page (files/resolve-path fs path-str)))

(describe "read-page"
  (with fs (test-fs/create-from test-pages))

  (it "sets the title"
    (should= "Test" (:title (do-read @fs "first"))))

  (it "sets published-at"
    (should= published-at (:published-at (do-read @fs "first"))))

  (it "sets the content"
    (should= "content body" (:content (do-read @fs "first"))))

  (it "sets content with multiple lines"
    (should= "one\ntwo" (:content (do-read @fs "second"))))

  (it "does not set published-at for unpublished pages"
    (should= nil (:published-at (do-read @fs "second"))))

  (it "includes arbitrary headers"
    (should= "World" (:hello (do-read @fs "second"))))

  (it "parses title headers with colons"
    (should= "test: two" (:title (do-read @fs "titleWithColon"))))

  (it "parses headers with no value"
    (should-not (:title (do-read @fs "titleWithNoValue")))))

(describe "data conversions"
  (with fs (test-fs/create-from test-pages))

  (it "adds published-at-str to page"
    (should= "31 October 2014" (page/retrieve-value (do-read @fs "first") :published-at-str {})))

  (it "adds summary"
    (should (> max-summary-length (count (:summary (do-read @fs "longPage"))))))

  (it "adds ellipsis to end of summary"
    (should (.endsWith (:summary (do-read @fs "longPage")) more-mark))))
