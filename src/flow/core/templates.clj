(ns flow.core.templates
  (:require [net.cgrand.enlive-html :as html]))


(html/defsnippet page-link "templates/page_link.html"
    [:#content]
    [replacements]
    [:#content html/any-node]  (html/replace-vars replacements))

(html/deftemplate page-list "templates/page_list.html"
  [pages]
  [:ol#list [:li html/first-of-type]] (html/clone-for [item pages]
                                                 [:li] (html/content (page-link item))))
(html/deftemplate page "templates/page.html"
  [replacements]
  [:#content html/any-node] (html/replace-vars replacements))
