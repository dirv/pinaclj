(ns pinaclj.theme-spec
  (require [speclj.core :refer :all]
           [pinaclj.test-fs :as test-fs]
           [pinaclj.theme :refer :all]))

(def sample-theme
  {:post.html :post-fn
   :override.html :override-fn
   :nested/page.html :nested/page-fn
   :both.xml :both-xml-fn
   :both.html :both-html-fn
   :only.xml :only-fn
   :cat.html :cat-fn})

(defn- determine [page-path]
  (determine-template sample-theme {:path page-path}))

(defn- determine-with-category [page-path category]
  (determine-template sample-theme {:path page-path
                                    :category category}))

(defn- determine-with-root [page-path src-root]
  (determine-template sample-theme {:path page-path
                                    :src-root src-root}))
(describe "determine-template"
  (it "chooses post if no override page found"
    (should= :post-fn (determine "/test.md")))
  (it "chooses override"
    (should= :override-fn (determine "/override.md")))
  (it "determines nested pages"
    (should= :nested/page-fn (determine "/nested/page.md")))
  (it "prefers html over other extensions"
    (should= :both-html-fn (determine "/both.md")))
  (it "chooses file without extension if present"
    (should= :only-fn (determine "/only.xml.md")))
  (it "chooses file based on category before it chooses default"
    (should= :cat-fn (determine-with-category "/test.md" :cat)))
  (it "chooses override even with category present"
    (should= :override-fn (determine-with-category "/override.md" :cat)))
  (it "chooses post if no override and no category is found"
    (should= :post-fn (determine-with-category "/test.md" :cat2)))
  (it "removes source root from path"
    (should= :override-fn (determine-with-root "/a/b/override.md" "/a/b"))))

(def test-files
  [{:path "a.html" :content "<html></html>"}
   {:path "b.html" :content ""}
   {:path "b.xml" :content ""}
   {:path "c.png" :content "!!!"}
   {:path "d" :content "!!!"}])

(describe "build-theme"
  (with fs (test-fs/create-from test-files))

  (it "reads HTML and XML files from fs"
    (should= '(:a.html :b.html :b.xml) (keys (build-theme @fs ""))))

  (it "loads template for each file"
    (should-contain :template-fn (val (first (build-theme @fs ""))))))
