(ns pinaclj.transforms.url-spec
  (:require [speclj.core :refer :all]
            [pinaclj.nio :as nio]
            [pinaclj.test-fs :as test-fs]
            [pinaclj.transforms.url :refer :all]))

(def fs
  (test-fs/test-fs))

(def page-with-url
  {:url "/test"
   :src-root "foo"
   :path "bar"})

(def page-with-path-only
  {:path (nio/get-path fs "path.md")})

(def page-with-src-root
  {:path (nio/get-path fs "/a/b/path.md")
   :src-root (nio/get-path fs "/a/b")})

(describe "add-url"
  (it "does not replace existing url if set"
    (should= "/test" (add-url page-with-url {})))
  (it "does not relativize if no src-root present"
    (should= "/path.html" (add-url page-with-path-only {})))
  (it "relativizes url if src-root is set"
    (should= "/path.html" (add-url page-with-src-root {}))))
