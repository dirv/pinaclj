(ns pinaclj.transforms.summary-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.summary :refer :all]
           [pinaclj.templates :as templates]))

(def short-page
  {:raw-content "One\n\nTwo"})

(def long-page
  {:raw-content (apply str (repeat 200 "ab "))})

(defn- summary-text []
  (templates/to-str (to-summary short-page {})))

(defn- summary-content []
  (templates/to-str (:content (first (to-summary long-page {})))))

(describe "to-summary"
  (it "converts markdown"
    (should-contain "<p>" (summary-text))
    (should-contain "</p>" (summary-text)))
  (it "only includes the first paragraph"
    (should-not-contain "Two" (summary-text)))
  (it "adds ellipsis to end of summary"
    (should (.endsWith (summary-content) more-mark)))
  (it "chops summary"
    (should (= max-summary-length (count (summary-content))))))
