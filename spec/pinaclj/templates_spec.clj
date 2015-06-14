(ns pinaclj.templates-spec
  (:require [speclj.core :refer :all]
            [clojure.pprint]
            [net.cgrand.enlive-html :as html]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.test-templates :as test-templates]
            [pinaclj.templates :refer :all]
            [pinaclj.date-time :as date]
            [pinaclj.page :as page]))


(def pages [{:url "/1" :title "First post" :content "first post content."}
             {:url "/2" :title "Second post" :content "second post content." }
             {:url "/3" :title "Third post" :content "<h1>third</h1> post content." :third-key "Hello, world!"
              :published-at (date/make 2014 11 30 0 0 0)
              }
            {:url "/4" :title "Fourth post" :content "published"
             :published-at (date/make 2014 12 31 0 0 0)
             }])

(defn render-page-link [page]
   (to-str (html/emit* (test-templates/page-link page))))

(defn render-page []
   (to-str (test-templates/page (first pages))))

(defn render-third-page []
  (to-str (test-templates/page (nth pages 2))))

(defn render-page-list []
   (to-str (test-templates/page-list pages)))

(defn render-feed []
  (to-str (test-templates/feed-list pages)))

(defn- apply-func-a [page]
  (page/set-lazy-value page
                       :func
                       (fn [page opts] (str "format=" (:format opts)))))

(defn render-func-params-page []
  (to-str (test-templates/func-params (apply-func-a (first pages)))))

(describe "page link snippet"
  (it "contains href"
    (should-contain "href=\"/1\"" (render-page-link (first pages))))

  (it "contains title"
    (should-contain "First post" (render-page-link (first pages)))))

(describe "page list"
  (it "contains correct number of items"
    (should= (count pages) (count (re-seq #"data-id=\"page-list-item\"" (render-page-list))))))

(describe "feed"
   (it "contains correct number of items"
     (should= (count pages) (count (re-seq #"data-id=\"page-list-item\"" (render-feed)))))
   (it "outputs updated date"
     (should-contain "2014-12-31T00:00:00Z</updated>" (render-feed))))

(describe "page"
  (it "renders title"
    (should-contain "First post" (render-page)))

  (it "renders content"
    (should-contain "first post content" (render-page)))

  (it "renders all keys"
    (should-contain "Hello, world!" (render-third-page))))

(describe "func params"
  (it "passes through parameters"
    (should-contain "format=123" (render-func-params-page))))
