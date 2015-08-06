(ns pinaclj.date-time
  (:import (java.time Instant ZoneId ZonedDateTime LocalDateTime)
           (java.time.format DateTimeFormatter)))

(defn to-readable-str [date-time format-string]
  (.format (.atZone date-time (ZoneId/of "UTC"))
           (DateTimeFormatter/ofPattern format-string)))

(defn from-str [value]
  (Instant/parse value))

(defn valid? [format-string]
  (try
    (DateTimeFormatter/ofPattern format-string)
    true
    (catch IllegalArgumentException e false)))

(defn now []
  (Instant/now))
