(ns pinaclj.page-spec
  (require [speclj.core :refer :all]
           [pinaclj.page :refer :all]))

(def ^:dynamic counter 0)

(def simple-page
  { :content "test" })

(defn- x-func [page opts]
  123)

(def simple-page
  {:x 123})

(def page-with-simple-func
  (set-lazy-value {}
                  :x (fn [page opts] 123)))

(def page-with-complex-func
  (set-lazy-value {:content "test"}
                  :y (fn [page opts] (:content page))))

(def page-with-differing-func-vals
  (set-lazy-value {}
                  :x (fn [page opts] counter)))

(describe "retrieve value"
  (it "retrieves a value"
    (should= 123 (retrieve-value simple-page :x {})))
  (it "computes a simple value"
    (should= 123 (retrieve-value page-with-simple-func :x {})))
  (it "computes a value based on page content"
    (should= "test" (retrieve-value page-with-complex-func :y {})))
  (it "memoizes"
    (retrieve-value page-with-differing-func-vals :x {})
    (binding [counter 1]
      (should= 0 (retrieve-value page-with-differing-func-vals :x {})))))
