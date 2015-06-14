(ns pinaclj.date-time
  (:import (java.time Instant ZoneId ZonedDateTime LocalDateTime)
           (java.time.format DateTimeFormatter)))

(defn make [year month day hour minute sec]
  (ZonedDateTime/of
    (LocalDateTime/of year month day hour minute sec)
    (ZoneId/of "UTC")))

(defn to-str [date-time]
  (.format date-time DateTimeFormatter/ISO_INSTANT))

(defn to-readable-str [date-time format-string]
  (.format date-time (DateTimeFormatter/ofPattern format-string)))

(defn from-str [value]
  (.atZone (Instant/parse value) (ZoneId/of "UTC")))

(defn valid? [format-string]
  (try
    (DateTimeFormatter/ofPattern format-string)
    true
    (catch IllegalArgumentException e false)))
