(ns pinaclj.core.pages.read-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.files :as files]
            [pinaclj.core.pages.read :refer :all]
            [pinaclj.core.pages.date-time :as date-time]
            [pinaclj.core.test-fs :as test-fs])
  (:import (java.time ZonedDateTime LocalDateTime Month ZoneId)))

(def published-at
  (date-time/make 2014 10 31 10 5 0))

(describe "read-page"
  (before (test-fs/create-file-system))

  (it "sets the title"
    (should= "Test" (:title (read-page "test"))))

  (it "sets published-at"
    (should= published-at (:published-at (read-page "test"))))

  (it "sets the content"
    (should= "content body" (:content (read-page "test"))))

  (it "sets content with multiple lines"
    (should= "one\ntwo" (:content (read-page "second"))))

  (it "does not set published-at for unpublished pages"
    (should= nil (:published-at (read-page "unpublished"))))

  (it "includes content for page without all headers"
    (should= "content" (:content (read-page "unpublished"))))

  (it "includes arbitrary headers"
    (should= "World" (:hello (read-page "test")))))

