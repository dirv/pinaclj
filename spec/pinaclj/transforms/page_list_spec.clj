(ns pinaclj.transforms.page-list-spec
  (require [speclj.core :refer :all]
           [pinaclj.transforms.page-list :refer :all]))

(defn- build-page-map [pages]
  (apply merge (map #(hash-map (:destination %) %) pages)))

(def all-pages
  [{:published-at 1 :title "c" :destination "url1"}
   {:published-at 2 :title "b" :destination "url2"}
   {:published-at 3 :title "a" :destination "url3"}])

(def all-pages-opts {:all-pages (build-page-map all-pages)})

(def pages {:pages ["url1" "url2" "url3"]})

(def some-pages {:pages ["url2" "url3"]})

(def generated-opts
  {:all-pages {"url1" {:published-at 4 :title "gen" :generated true}}})

(def category-pages
  [{:category "cat" :destination "a"}
   {:category "cat" :destination "b"}
   {:destination "c"}])

(def category-opts
  {:all-pages (build-page-map category-pages)
   :category "cat"})

(def category-parent {:pages (keys (:all-pages category-opts))})

(def ordered-pages
  [{:destination "a" :order 3}
   {:destination "b" :order 1}
   {:destination "c" :order 2}])

(def ordered-opts {:all-pages (build-page-map ordered-pages)
                   :order-by "order"})

(def reverse-ordered-opts (assoc ordered-opts :reverse "true"))

(def ordered-parent {:pages (keys (:all-pages ordered-opts))})

(describe "clone-pages"
  (describe "with :pages set"
    (it "does not order provided page list"
      (should= pages (clone-pages pages all-pages-opts)))
    (it "lists only subset if page specificiations is given"
      (should= some-pages (clone-pages some-pages all-pages-opts)))
    (it "does not include generated pages"
      (should= 0 (count (:pages (clone-pages {} generated-opts))))))

  (describe "with :category opt"
    (it "filters all-pages when category opt is included"
      (should= ["a" "b"] (:pages (clone-pages category-parent category-opts)))))

  (describe "with :order-by opt"
    (it "orders according to the specified attribute"
      (should= ["b" "c" "a"]
        (:pages (clone-pages ordered-parent ordered-opts))))
    (it "orders in reverse when reverse specified"
      (should= ["a" "c" "b"]
        (:pages (clone-pages ordered-parent reverse-ordered-opts))))))


