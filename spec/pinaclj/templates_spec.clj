(ns pinaclj.templates-spec
  (:require [speclj.core :refer :all]
            [clojure.pprint]
            [net.cgrand.enlive-html :as html]
            [pinaclj.templates :refer :all]))


(def pages [{:url "/1" :title "First post" :content "first post content."}
             {:url "/2" :title "Second post" :content "second post content." }
             {:url "/3" :title "Third post" :content "<h1>third</h1> post content." :third-key "Hello, world!"}])

(defn render-page-link []
   (apply str (html/emit* (page-link (first pages)))))

(describe "page link snippet"
  (it "contains href"
    (should-contain "href=\"/1\"" (render-page-link)))

  (it "contains title"
    (should-contain "First post" (render-page-link))))

(defn render-page-list []
   (apply str (page-list pages)))

(describe "page list"
  (it "contains correct number of items"
    (should= 3 (count (re-seq #"data-id=\"page-list-item\"" (render-page-list))))))

(def page
  (build-page-func "templates/page.html"))

(defn render-page []
   (apply str (page (first pages))))

(defn render-third-page []
  (apply str (page (nth pages 2))))

(describe "page"
  (it "renders title"
    (should-contain "First post" (render-page)))

  (it "renders content"
    (should-contain "first post content" (render-page)))

  (it "renders all keys"
    (should-contain "Hello, world!" (render-third-page)))

  (it "does not escape html in contet"
    (should-contain "<h1>third</h1>" (render-third-page))))
