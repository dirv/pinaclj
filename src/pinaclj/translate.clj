(ns pinaclj.translate
  (:require [taoensso.tower :as tower]))

(def t
  (tower/make-t {:dictionary "dictionary.clj"
                 :dev-mode? true
                 :fallback-locale :en}))
