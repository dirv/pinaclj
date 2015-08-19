(ns pinaclj.transforms.templated-content-spec
  (require [speclj.core :refer :all]
           [net.cgrand.enlive-html :as html]
           [pinaclj.transforms.transforms :as transforms]
           [pinaclj.transforms.templated-content :refer :all]))

(def quote-page
  {:content "'"})

(def nested-page
  (transforms/apply-all {:url "nested/test.html"
                         :content (html/html-snippet "<link src=styles.css />")}))

(def template
  {:template-func (fn [x] (:content x))})

(describe "do-template"
  (it "transforms quotes"
    (should= "‘" (do-template quote-page {:template template})))

  (it "transforms relative urls"
    (should-contain "../styles.css" (do-template nested-page {:template template}))))
