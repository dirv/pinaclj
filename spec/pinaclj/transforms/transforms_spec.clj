(ns pinaclj.transforms.transforms-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.transforms :refer :all]
           [pinaclj.date-time :as date-time]
           [pinaclj.transforms.summary :as summary]
           [pinaclj.page :as page]))

(def page
  (apply-all
    {:raw-content (apply str (repeat 200 "ab "))
     :published-at (date-time/from-str "2014-10-31T10:05:00Z")}))

(defn- published-at []
  (page/retrieve-value page :published-at {}))

(defn- summary []
  (page/retrieve-value page :summary {}))

(describe "data conversions"

  (it "adds published-at to page"
    (should= "31 October 2014" (published-at)))

  (it "adds summary"
    (should (> summary/max-summary-length (count (summary))))))
