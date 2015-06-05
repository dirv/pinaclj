(ns pinaclj.transforms.summary-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.summary :refer :all]
           [pinaclj.page :as page]
           [pinaclj.templates :as templates]))

(def short-page
  (apply-transform {:raw-content "One\n\nTwo"}))

(def long-page
  (apply-transform {:raw-content (apply str (repeat 200 "ab "))}))

(defn- summary-text []
  (templates/to-str (page/retrieve-value (apply-transform short-page) :summary {})))

(defn- summary-content []
  (templates/to-str (:content (first (page/retrieve-value (apply-transform long-page) :summary {})))))

(describe "apply-transform"
  (it "converts markdown"
    (should-contain "<p>" (summary-text))
    (should-contain "</p>" (summary-text)))
  (it "only includes the first paragraph"
    (should-not-contain "Two" (summary-text)))
  (it "adds ellipsis to end of summary"
    (should (.endsWith (summary-content) more-mark)))
  (it "chops summary"
    (should (= max-summary-length (count (summary-content))))))
