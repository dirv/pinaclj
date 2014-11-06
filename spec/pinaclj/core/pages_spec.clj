(ns pinaclj.core.pages-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.nio :as nio]
            [pinaclj.core.pages :refer :all]
            [pinaclj.core.test-fs :as test-fs])
  (:import (java.time ZonedDateTime LocalDateTime Month ZoneId)))


(defn- get-page [page-path]
  (let [fs-root (test-fs/create-file-system)]
    (let [page (read-page (nio/child-path fs-root page-path) fs-root)]
      page)))

(describe "read-page"
  (it "sets the title"
    (should= "Test" (:title (get-page "test"))))
  (it "sets published-at"
    (should= (ZonedDateTime/of
                (LocalDateTime/of 2014 Month/OCTOBER 31 10 5 0)
                (ZoneId/of "UTC"))
              (:published-at (get-page "test"))))
  (it "sets the content"
    (should= '("content body") (:content (get-page "test"))))
  (it "sets content with multiple lines"
    (should= '("one" "two") (:content (get-page "second"))))
  (it "does not set published-at for unpublished pages"
    (should= nil (:published-at (get-page "unpublished"))))
  (it "includes content for page without all headers"
    (should= '("content") (:content (get-page "unpublished")))))

(defn make-page [path page]
  (let [fs-root (test-fs/create-file-system)
        path    (nio/child-path fs-root path)]
    (write-page path page)
    (nio/content path)))

(def published-at
  (ZonedDateTime/of
    (LocalDateTime/of 2014 Month/OCTOBER 30 10 5 0)
    (ZoneId/of "UTC")))

(describe "write-page"
  (it "writes the title"
    (should-contain "Title: Title" (make-page "title_page" {:title "Title"})))

  (it "writes the published_at"
    (should-contain "Published-at: 2014-10-30T10:05:00Z" (make-page "pub_page" {:published-at published-at})))

  (it "writes the content"
    (should-contain "content yo" (make-page "content_page" {:content "content yo"})))

  (it "generates published at if not present"
    (should-contain "Published-at: " (make-page "pub_page2" {:content "content yo"})))

  (it "writes page in correct format"
    (should= "Title: title\nPublished-at: 2014-10-30T10:05:00Z\n\ncontent" 
             (make-page "format_page" {:title "title" :published-at published-at :content "content"}))))
