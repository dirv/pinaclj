(ns pinaclj.transforms.href-spec
  (:require [speclj.core :refer :all]
            [pinaclj.page :as page]
            [pinaclj.transforms.href :refer :all]))

(def twitter-page {:twitter "twitter-href"})

(def facebook-page {:facebook "facebook-href"})

(def lazy-page (page/set-lazy-value {} :twitter (fn [page opts] "lazy-twitter-href")))

(describe "set-href"
          (it "appends the href attribute"
              (should-contain :href
                              (:attrs (set-href twitter-page {:key "twitter"}))))
          (it "sets the href to the attribute"
              (should= "twitter-href"
                       (get-in (set-href twitter-page {:key "twitter"}) [:attrs :href])))
          (it "sets the href to the attribute for facebook"
              (should= "facebook-href"
                       (get-in (set-href facebook-page {:key "facebook"}) [:attrs :href])))
          (it "handles lazy pages"
              (should= "lazy-twitter-href"
                       (get-in (set-href lazy-page {:key "twitter"}) [:attrs :href]))))
