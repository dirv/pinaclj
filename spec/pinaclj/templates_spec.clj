(ns pinaclj.templates-spec
  (:require [speclj.core :refer :all]
            [pinaclj.templates :refer :all]
            [pinaclj.templates :refer :all]
            [pinaclj.test-templates :as samples]
            [pinaclj.page :as page]
            [net.cgrand.enlive-html :as html]))

(defn- to-snippet [body]
  (html/html-snippet (str "<html><body>" body "</body></html>")))

(defn- template [body all-pages]
  (build-page-func (to-snippet body) all-pages))

(defn- do-replace [template-body-str page all-pages]
  (to-str ((template template-body-str all-pages) page)))

(def page-template
  "<p data-id=a /><p data-id=b /><p data-id=c />")

(def page-list-template
  "<ol data-id=page-list ><li><p data-id= title /></li></ol>")

(def nested-page-template
  "<div data-id=latest > <p data-id=title ></p> </div>")

(def deep-nested-page-template
  "<div data-id=latest > <div> <div> <p data-id=title ></p> </div> </div> </div>")

(def func-params-template
  "<p data-id=func data-format=123 />")

(def string-value-page
  {:a "testA" :b "testB" :c "testC"})

(def attribute-page
  {:a {:attrs {:one "a" :two "b"}
       :content "testing" }})

(def all-referenced-pages
  {:url1 {:title "val1"}
   :url2 {:title "val2"}})

(def page-with-child-pages
  {:page-list {:pages [:url1 :url2]}})

(def nested-page
  {:latest {:page :url1}
   :title "not expected"})

(def func-page
  (page/set-lazy-value {}
                       :func
                       (fn [page opts] (str "format=" (:format opts)))))

(def print-page-list-func
  (page/set-lazy-value {}
                       :func
                       (fn [page opts] (apply str (keys (:all-pages opts))))))

(def delete-page-template
  "<p data-id=if-exists > bye </p>")

(def delete-page
  {:if-exists {:delete true}})

(describe "build-page-func"
  (it "transforms string values"
    (let [result (do-replace page-template string-value-page {})]
      (should-contain "testA" result)
      (should-contain "testB" result)
      (should-contain "testC" result)))

  (it "transforms attributes and content"
    (let [result (do-replace page-template attribute-page {})]
      (should-contain "one=\"a\"" result)
      (should-contain "two=\"b\"" result)
      (should-contain ">testing</p>" result)))

  (it "includes all child pages"
    (let [result (do-replace page-list-template page-with-child-pages all-referenced-pages)]
      (should-contain "val1" result)
      (should-contain "val2" result)))

  (it "contains correct number of child items"
    (let [result (do-replace page-list-template page-with-child-pages all-referenced-pages)]
      (should-contain "val1" result)
      (should= 1 (count (re-seq #"data-id=\"page-list\"" result)))
      (should= 2 (count (re-seq #"/li" result)))))

  (it "transforms nested page"
    (let [result (do-replace nested-page-template nested-page all-referenced-pages)]
      (should-contain "val1" result)
      (should-not-contain "not expected" result)))

  (it "transforms deep nested page"
    (let [result (do-replace deep-nested-page-template nested-page all-referenced-pages)]
      (should-contain "val1" result)
      (should-not-contain "not expected" result)))

  (it "transforms using page functions with data attributes"
    (let [result (do-replace func-params-template func-page {})]
      (should-contain "format=123" result)))

  (it "sends all-pages context to page funcs"
    (let [result (do-replace func-params-template print-page-list-func all-referenced-pages)]
      (should-contain ":url1:url2" result)))

  (it "deletes nodes"
    (should-not-contain "bye" (do-replace delete-page-template delete-page {}))))

(describe "build-page-list-opts"
  (def page-list-opts
    "<ol data-id=page-list data-max-pages=3 />")

  (def page-list-opts-with-category
    "<ol data-id=page-list data-category=test />")

  (it "extracts max pages from page list"
    (should= 3 (:max-pages (build-page-list-opts (html/html-snippet page-list-opts)))))

  (it "sets :owns-child-pages"
    (should= true (:owns-child-pages? (build-page-list-opts (html/html-snippet page-list-opts)))))

  (it "does not set :owns-child-pages? when :category is set"
    (should= false (:owns-child-pages? (build-page-list-opts (html/html-snippet page-list-opts-with-category))))))

(describe "build-template"
  (def test-split-page
    {:page-list {:pages [:url1]}})

  (defn- build-split-list-template []
    (build-template (samples/stream "split_list.html")))

  (it "parses max pages"
    (should= 3 (:max-pages (build-split-list-template))))
  (it "sets template func"
    (let [template-func ((:template-fn (build-split-list-template)) all-referenced-pages)]
      (should-contain "val1" (to-str (template-func test-split-page))))))

(def multiple-selector-template
  "<p data-id=func /> <p data-id=func />")

(describe "page with multiple occurrences of func"
  (it "lists selector only once"
    (should= ["func"] (find-all-functions (to-snippet multiple-selector-template)))))

(def set-only-attr-template
  "<p data-id=a data-set=attrs />")

(def set-only-content-template
  "<p data-id=a data-set=content />")

(def attr-page
  {:a {:attrs {:one "a"}
       :content "testing" }})

(describe "template with specific include"
  (it "sets attrs when attrs specified"
    (let [result (do-replace set-only-attr-template attribute-page {})]
      (should-contain "one=\"a\"" result)
      (should-not-contain ">testing</p>" result)))
  (it "sets content when content specified"
    (let [result (do-replace set-only-content-template attribute-page {})]
      (should-not-contain "one=\"a\"" result)
      (should-contain ">testing</p>" result))))
