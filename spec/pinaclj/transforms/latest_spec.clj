(ns pinaclj.transforms.latest-spec
  (require [speclj.core :refer :all]
           [pinaclj.date-time-helpers :as dt]
           [pinaclj.transforms.latest :refer :all]))

(def earliest
  {:published-at (dt/make 2015 01 01 01 01 01)})

(def latest
  {:published-at (dt/make 2016 01 01 01 01 01)})

(def parent
  {:pages [earliest latest]})

(describe "get-latest"
  (it "gets latest"
    (should= latest (:page (get-latest parent {})))))
