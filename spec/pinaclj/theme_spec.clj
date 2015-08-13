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

(describe "determine-template"
  (it "chooses post if no override page found"
    (should= :post.html (determine-template sample-theme "/test.md")))
  (it "chooses override"
    (should= :override.html (determine-template sample-theme "/override.md")))
  (it "determines nested pages"
    (should= :nested/page.html (determine-template sample-theme "/nested/page.md")))
  (it "prefers html over other extensions"
    (should= :both.html (determine-template sample-theme "/both.md")))
  (it "chooses file without extension if present"
    (should= :only.xml (determine-template sample-theme "/only.xml.md"))))

