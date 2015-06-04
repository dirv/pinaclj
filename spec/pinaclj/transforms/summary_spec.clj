(ns pinaclj.transforms.summary-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.summary :refer :all]
           [pinaclj.page :as page]))

(def page
  (apply-transform {:raw-content "### Heading"}))

(defn- page-with-transform []
  (apply-transform page))

(describe "apply-transform"
  (it "converts markdown"
    (should= "<h3>Heading</h3>"
      (page/retrieve-value (page-with-transform) :summary {}))))
