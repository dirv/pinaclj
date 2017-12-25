(ns pinaclj.transforms.content-spec
  (:require [speclj.core :refer :all]
            [pinaclj.templates :as templates]
            [pinaclj.transforms.content :refer :all]))

(def page
  {:raw-content "###Markdown header"})

(describe "add-content"
  (it "renders markdown"
    (should= "<h3>Markdown header</h3>" (templates/to-str (add-content page {})))))
