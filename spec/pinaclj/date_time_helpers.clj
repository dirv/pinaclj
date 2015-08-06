(ns pinaclj.date-time-helpers
  (:import (java.time Instant ZoneId ZonedDateTime LocalDateTime))
  (:require [speclj.core :refer :all]))

(defn make [year month day hour minute sec]
  (Instant/from (ZonedDateTime/of
    (LocalDateTime/of year month day hour minute sec)
    (ZoneId/of "UTC"))))
