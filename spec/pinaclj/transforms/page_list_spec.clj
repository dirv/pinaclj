(ns pinaclj.transforms.page-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.page-list :refer :all]))

(def all-pages
  {:url1 {:published-at 1 :title "c"}
   :url2 {:published-at 2 :title "b"}
   :url3 {:published-at 3 :title "a"}})

(def all-pages-opts
  {:all-pages all-pages})

(def pages
  {:pages [:url1 :url2 :url3]})

(def some-pages
  {:pages [:url2 :url3]})

(def generated-opts
  {:all-pages {:url1 {:published-at 4 :title "gen" :generated true}}})

(describe "clone-pages"
  (it "does not order provided page list"
    (should= pages (clone-pages pages all-pages-opts)))
  (it "lists all pages if no page specification is given"
    (should= 3 (count (:pages (clone-pages {} all-pages-opts)))))
  (it "orders all pages if no page specification is given"
    (should= [:url3 :url2 :url1] (:pages (clone-pages {} all-pages-opts))))
  (it "lists only subset if page specificiations is given"
    (should= some-pages (clone-pages some-pages all-pages-opts)))
  (it "does not include generated pages"
    (should= 0 (count (:pages (clone-pages {} generated-opts))))))
