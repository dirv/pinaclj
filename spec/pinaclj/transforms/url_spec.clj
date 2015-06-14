(ns pinaclj.transforms.url-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.url :refer :all]))

(def page-with-url
  {:url "test"
   :src-root "foo"
   :path "bar"})

(describe "add-url"
  (it "does not replace existing url if set"
    (should= "test" (add-url page-with-url {}))))
