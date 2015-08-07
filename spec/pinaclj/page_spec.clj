(ns pinaclj.page-spec
  (require [speclj.core :refer :all]
           [pinaclj.page :refer :all]
           [pinaclj.files :as files]
           [pinaclj.nio :as nio]
           [pinaclj.test-fs :as test-fs]))

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

(def page-with-both
  (set-lazy-value {:x "test"}
                  :y (fn [page opts] "test")))

(def page-with-override
  (set-lazy-value {:x "foo"}
                  :x (fn [page opts] "bar")))

(describe "retrieve-value"
  (it "retrieves a value"
    (should= 123 (retrieve-value simple-page :x {})))
  (it "computes a simple value"
    (should= 123 (retrieve-value page-with-simple-func :x {})))
  (it "computes a value based on page content"
    (should= "test" (retrieve-value page-with-complex-func :y {})))
  (it "computes an override value"
    (should= "bar" (retrieve-value page-with-override :x {})))
  (it "memoizes"
    (retrieve-value page-with-differing-func-vals :x {})
    (binding [counter 1]
      (should= 0 (retrieve-value page-with-differing-func-vals :x {})))))

(describe "all-keys"
  (it "gets keys of both static and computed values"
    (should= '(:x :y) (all-keys page-with-both))))

(defn- read-file [fs path]
  (clojure.string/join "\n" (files/read-lines (nio/resolve-path fs path))))

(defn- file-exists? [fs path]
  (nio/exists? (nio/resolve-path fs path)))

(describe "write"
  (with fs (test-fs/create-from []))

  (def page-to-write
    {:path "pages/test.md"
     :raw-content "test"
     :a "a"
     :b "b"})

  (before (write-page page-to-write @fs))

  (it "writes page to disk"
    (should (file-exists? @fs "pages/test.md")))
  (it "writes correct content to disk"
    (should (.endsWith (read-file @fs "pages/test.md") "---\ntest")))
  (it "writes all headers"
    (should-contain "a: a\n" (read-file @fs "pages/test.md"))
    (should-contain "b: b\n" (read-file @fs "pages/test.md")))
  (it "does not write out path or raw-content as headers"
    (should-not-contain "path: " (read-file @fs "pages/test.md"))
    (should-not-contain "raw-content: " (read-file @fs "pages/test.md")))

  (def previously-written-page
    {:path "pages/previous.md"
     :raw-content "test"
     :a "a" :b "b" :c "c"
     :read-headers [:b :a]})

  (before (write-page previously-written-page @fs))

  (it "writes previously written headers first and in order"
    (should (re-find #"(?m)b:.*\na:.*\nc:" (read-file @fs "pages/previous.md")))))
