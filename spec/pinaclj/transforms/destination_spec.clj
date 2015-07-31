(ns pinaclj.transforms.destination-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.destination :refer :all]))

(def url-page {:url "/a/blog/page.html" :path "test.md"})

(def no-extension {:url "blog/page/" })

(describe "add-destination"
  (it "uses the url header if one is present"
    (should= "a/blog/page.html" (add-destination url-page {})))
  (it "adds html extension if it isn't present"
    (should= "blog/page/index.html" (add-destination no-extension {}))))
