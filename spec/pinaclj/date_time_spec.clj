(ns pinaclj.date-time-spec
  (:require [speclj.core :refer :all]
            [pinaclj.date-time :refer :all]))

(describe "date-time"
  (it "makes a date"
    (should= 2014 (.getYear (make 2014 12 14 21 30 00))))

  (it "converts to string and back"
    (should= "2014-10-31T10:05:00Z" (to-str (from-str "2014-10-31T10:05:00Z"))))

  (it "converts datetime to readable format"
    (should= "5 February 2015", (to-readable-str (from-str "2015-02-05T10:05:00Z")))))
