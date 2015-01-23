(ns pinaclj.core.pages.write-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.files :as files]
            [pinaclj.core.pages.write :refer :all]
            [pinaclj.core.pages.date-time :as date-time]
            [pinaclj.core.test-fs :as test-fs]))

(def published-at
  (date-time/make 2014 10 31 10 5 0))

(defn make-page [fs path-str page]
  (write-page  (files/resolve-path fs path-str) page)
  (files/content (files/resolve-path fs path-str)))

(describe "write-page"
  (with fs (test-fs/create-empty))

  (it "writes the title"
    (should-contain "title: Title" (make-page @fs "title_page" {:headers {:title "Title"}})))

  (it "writes the published_at"
    (should-contain "published-at: 2014-10-31T10:05:00Z" (make-page @fs "pub_page" {:headers {:published-at published-at}})))

  (it "writes the content"
    (should-contain "content yo" (make-page @fs "content_page" {:content "content yo"})))

  (it "excludes published at if not present"
    (should-not-contain "published-at: " (make-page @fs "pub_page2" {:content "content yo"})))

  (it "writes arbitrary headers"
    (should-contain "hello: world" (make-page @fs "helloworld" {:content "content" :headers {:hello "world"}})))

  (it "writes page in correct format"
    (should= "title: title\npublished-at: 2014-10-31T10:05:00Z\n\ncontent"
             (make-page @fs "format_page" {:headers {:title "title" :published-at published-at} :content "content"}))))
