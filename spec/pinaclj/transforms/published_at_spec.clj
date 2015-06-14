(ns pinaclj.transforms.published-at-spec
  (require [speclj.core :refer :all]
           [pinaclj.date-time :as dt]
           [pinaclj.transforms.published-at :refer :all]))

(def page
  {:published-at (dt/from-str "2015-02-05T10:05:00Z")})

(describe "to-readable-str"
  (it "converts with default date format"
    (should= "5 February 2015" (to-readable-str page {})))
  (it "converts using specified format"
    (should= "February 2015" (to-readable-str page {:format "MMMM yyyy"})))
  (it "does not convert invalid formats"
    (should= "5 February 2015" (to-readable-str page {:format "blah"}))))
