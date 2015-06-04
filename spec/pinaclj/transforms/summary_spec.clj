(ns pinaclj.transforms.summary-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.summary :refer :all]
           [pinaclj.page :as page]
           [pinaclj.templates :as templates]))

(def page
  (apply-transform {:raw-content "One\n\nTwo"}))

(defn- page-with-transform []
  (apply-transform page))

(defn- summary-text []
  (templates/to-str (page/retrieve-value (page-with-transform) :summary {})))

(describe "apply-transform"
  (it "converts markdown"
    (should-contain "<p>" (summary-text))
    (should-contain "</p>" (summary-text)))
  (it "only includes the first paragraph"
    (should-not-contain "Two" (summary-text))))
