(ns pinaclj.core.pages.read-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.nio :as nio]
            [pinaclj.core.pages.read :refer :all]
            [pinaclj.core.test-fs :as test-fs])
  (:import (java.time ZonedDateTime LocalDateTime Month ZoneId)))

(defn- get-page [page-path]
  (let [fs-root (test-fs/create-file-system)]
    (let [page (read-page (nio/child-path fs-root page-path) fs-root)]
      page)))

(def published-at
  (ZonedDateTime/of
    (LocalDateTime/of 2014 Month/OCTOBER 31 10 5 0)
    (ZoneId/of "UTC")))

(describe "read-page"
  (it "sets the title"
    (should= "Test" (:title (get-page "test"))))
  (it "sets published-at"
    (should= published-at (:published-at (get-page "test"))))
  (it "sets the content"
    (should= "content body" (:content (get-page "test"))))
  (it "sets content with multiple lines"
    (should= "one\ntwo" (:content (get-page "second"))))
  (it "does not set published-at for unpublished pages"
    (should= nil (:published-at (get-page "unpublished"))))
  (it "includes content for page without all headers"
    (should= "content" (:content (get-page "unpublished"))))
  (it "includes arbitrary headers"
    (should= "World" (:hello (get-page "test")))))

