(ns pinaclj.core.pages.write-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.nio :as nio]
            [pinaclj.core.pages.write :refer :all]
            [pinaclj.core.test-fs :as test-fs])
  (:import (java.time ZonedDateTime LocalDateTime Month ZoneId)))

(def published-at
  (ZonedDateTime/of
    (LocalDateTime/of 2014 Month/OCTOBER 31 10 5 0)
    (ZoneId/of "UTC")))


(defn make-page [path page]
  (let [fs-root (test-fs/create-file-system)
        path    (nio/child-path fs-root path)]
    (write-page path page)
    (nio/content path)))

(describe "write-page"
  (it "writes the title"
    (should-contain "title: Title" (make-page "title_page" {:headers {:title "Title"}})))

  (it "writes the published_at"
    (should-contain "published-at: 2014-10-31T10:05:00Z" (make-page "pub_page" {:headers {:published-at published-at}})))

  (it "writes the content"
    (should-contain "content yo" (make-page "content_page" {:content "content yo"})))

  (it "excludes published at if not present"
    (should-not-contain "published-at: " (make-page "pub_page2" {:content "content yo"})))

  (it "writes arbitrary headers"
    (should-contain "hello: world" (make-page "helloworld" {:content "content" :headers {:hello "world"}})))

  (it "writes page in correct format"
    (should= "title: title\npublished-at: 2014-10-31T10:05:00Z\n\ncontent"
             (make-page "format_page" {:headers {:title "title" :published-at published-at} :content "content"}))))
