(ns pinaclj.transforms.modified-spec
  (:require [speclj.core :refer :all]
            [pinaclj.page :as page]
            [pinaclj.transforms.modified :refer :all]))

(def simple-page
  {:modified 1})

(def page-list
  {:pages '({:modified 1} {:modified 2})})

(defn modified [page]
  (page/retrieve-value (apply-transform page) :modified {}))

(describe "apply transform"
  (it "uses modified date for simple page"
    (should= 1 (modified simple-page)))

  (it "uses latest modified date for page list"
    (should= 2 (modified page-list))))

