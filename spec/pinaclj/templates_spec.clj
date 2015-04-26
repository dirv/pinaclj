(ns pinaclj.templates-spec
  (:require [speclj.core :refer :all]
            [clojure.pprint]
            [net.cgrand.enlive-html :as html]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.test-templates :as test-templates]
            [pinaclj.templates :refer :all]))


(def pages [{:url "/1" :title "First post" :content "first post content."}
             {:url "/2" :title "Second post" :content "second post content." }
             {:url "/3" :title "Third post" :content "<h1>third</h1> post content." :third-key "Hello, world!"}
            {:url "/4" :title "Fourth post" :content "published" :published-at-str "31 December 2014"}])

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

(describe "page link snippet"
  (it "contains href"
    (should-contain "href=\"/1\"" (render-page-link (first pages))))

  (it "contains title"
    (should-contain "First post" (render-page-link (first pages))))

  (it "contains published-at-str"
    (should-contain "31 December 2014" (render-page-link (nth pages 3)))))

(describe "page list"
  (it "contains correct number of items"
    (should= (count pages) (count (re-seq #"data-id=\"page-list-item\"" (render-page-list))))))

(describe "feed"
   (it "contains correct number of items"
     (should= (count pages) (count (re-seq #"data-id=\"page-list-item\"" (render-feed))))))

(describe "page"
  (it "renders title"
    (should-contain "First post" (render-page)))

  (it "renders content"
    (should-contain "first post content" (render-page)))

  (it "renders all keys"
    (should-contain "Hello, world!" (render-third-page))))

