(ns pinaclj.transforms.num-pages-spec
  (:require [speclj.core :refer :all]
            [pinaclj.transforms.num-pages :refer :all]))


(def no-child-pages {})

(def two-children {:pages ["a" "b"]})

(describe "calculate-num-pages"
  (it "returns 0 if :pages is not set"
    (should= 0 (calculate-num-pages no-child-pages {})))
  (it "returns count if :pages is set"
    (should= 2 (calculate-num-pages two-children {}))))

