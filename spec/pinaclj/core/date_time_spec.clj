(ns pinaclj.core.write-spec
  (:require [speclj.core :refer :all]
            [pinaclj.core.date-time :refer :all]))

(describe "date-time"
  (it "makes a date"
    (should= 2014 (.getYear (make 2014 12 14 21 30 00))))

  (it "converts to string and back"
    (should= "2014-10-31T10:05:00Z" (to-str (from-str "2014-10-31T10:05:00Z")))))
