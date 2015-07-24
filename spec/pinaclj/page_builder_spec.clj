(ns pinaclj.page-builder-spec
  (require [speclj.core :refer :all]
           [pinaclj.page-builder :refer :all]))

(def page-a {:title "a"})
(def page-b {:title "b"})
(def page-c {:title "c"})

(def pages
  {:pages [page-a page-b page-c] :url "index.html"})

(def template-with-no-max
  nil)

(def template-with-low-max
  {:max-pages 2})

(def template-with-high-max
  {:max-pages 3})

(defn- divide-pages []
  (divide pages template-with-low-max))

(describe "divide"
  (it "does not divide pages without max-pages set"
    (should= [pages] (divide pages template-with-no-max)))
  (it "does not divide pages which does not hit max-pages"
    (should= 1 (count (divide pages template-with-high-max))))
  (it "divides pages with low max-pages"
    (should= 2 (count (divide pages template-with-low-max))))
  (it "contains pages in order when dividing pages"
    (should= [[page-a page-b] [page-c]] (map :pages (divide-pages))))
  (it "sets start page when dividing"
    (should= [0 2] (map :start (divide pages template-with-low-max))))
  (it "modifies all but first urls"
    (should= ["index.html" "index-2.html"] (map :url (divide-pages))))
  (it "adds previous link to second page only"
    (should= [nil "index.html"] (map :previous (divide-pages))))
  (it "adds next link to first page only"
    (should= ["index-2.html" nil] (map :next (divide-pages)))))
