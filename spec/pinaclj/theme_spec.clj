(ns pinaclj.theme-spec
  (require [speclj.core :refer :all]
           [pinaclj.theme :refer :all]))

(def sample-theme
  {:post.html ""
   :override.html ""
   :nested/page.html ""
   :both.xml ""
   :both.html ""
   :only.xml ""})

(defn- determine [page-path]
  (determine-template sample-theme {:path page-path}))

(describe "determine-template"
  (it "chooses post if no override page found"
    (should= :post.html (determine "/test.md")))
  (it "chooses override"
    (should= :override.html (determine "/override.md")))
  (it "determines nested pages"
    (should= :nested/page.html (determine "/nested/page.md")))
  (it "prefers html over other extensions"
    (should= :both.html (determine "/both.md")))
  (it "chooses file without extension if present"
    (should= :only.xml (determine "/only.xml.md"))))

