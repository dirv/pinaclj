(ns pinaclj.templates-spec
  (:require [speclj.core :refer :all]
            [pinaclj.templates :refer :all]
            [pinaclj.templates :refer :all]
            [pinaclj.test-templates :as samples]
            [pinaclj.page :as page]
            [net.cgrand.enlive-html :as html]))

(defn- template [body]
  (build-page-func (html/html-snippet (str "<html><body>" body "</body></html>"))))

(defn- do-replace [template-body-str page]
  (to-str ((template template-body-str) page)))

(def page-template
  "<p data-id=a /><p data-id=b /><p data-id=c />")

(def page-list-template
  "<ol><li data-id=page-list ><p data-id= test /></li></ol>")

(def nested-page-template
  "<div data-id=latest > <p data-id=title ></p> </div>")

(def func-params-template
  "<p data-id=func data-format=123 />")

(def string-value-page
  {:a "testA" :b "testB" :c "testC"})

(def attribute-page
  {:a {:attrs {:one "a" :two "b"}
       :content "testing" }})

(def page-with-child-pages
  {:page-list {:pages [{:test "val1"}
                       {:test "val2"}]}})

(def nested-page
  {:latest {:page {:title "test-title"}}
   :title "not expected"})

(def func-page
  (page/set-lazy-value {}
                       :func
                       (fn [page opts] (str "format=" (:format opts)))))

(describe "build-page-func"
  (it "transforms string values"
    (let [result (do-replace page-template string-value-page)]
      (should-contain "testA" result)
      (should-contain "testB" result)
      (should-contain "testC" result)))

  (it "transforms attributes and content"
    (let [result (do-replace page-template attribute-page)]
      (should-contain "one=\"a\"" result)
      (should-contain "two=\"b\"" result)
      (should-contain ">testing</p>" result)))

  (let [result (do-replace page-list-template page-with-child-pages)]
    (it "includes all child pages"
      (should-contain "val1" result)
      (should-contain "val2" result))
    (it "contains correct number of child items"
      (should= 2 (count (re-seq #"data-id=\"page-list\"" result)))))

  (it "transforms nested page"
    (let [result (do-replace nested-page-template nested-page)]
      (should-contain "test-title" result)
      (should-not-contain "not expected" result)))

  (it "transforms using page functions with data attributes"
    (let [result (do-replace func-params-template func-page)]
      (should-contain "format=123" result))))

(describe "build-page-list-opts"
  (def page-with-opts
    "<ol data-id=page-list data-max-pages=3 />")

  (it "extracts max pages from page list"
    (should= 3 (:max-pages (build-page-list-opts (html/html-snippet page-with-opts))))))

(describe "build-template"
  (def test-split-page
    {:page-list {:pages [{:title "hello world"}]}})

  (let [template (build-template (samples/stream "split_list.html"))]
    (it "parses max pages"
      (should= 3 (:max-pages template)))
    (it "sets template func"
      (should-contain "hello world" (to-str ((:template-func template)
                                             test-split-page))))))
