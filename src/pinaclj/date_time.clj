(ns pinaclj.date-time
  (:import (java.time Instant ZoneId ZonedDateTime LocalDateTime)
           (java.time.format DateTimeFormatter)))

(defn make [year month day hour minute sec]
  (ZonedDateTime/of
    (LocalDateTime/of year month day hour minute sec)
    (ZoneId/of "UTC")))

(defn to-str [date-time]
  (.format date-time DateTimeFormatter/ISO_INSTANT))

(defn from-str [value]
  (.atZone (Instant/parse value) (ZoneId/of "UTC")))
