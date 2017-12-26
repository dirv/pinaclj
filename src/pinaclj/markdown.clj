(ns pinaclj.markdown
  (:require [net.cgrand.enlive-html :as html])
  (:import [com.vladsch.flexmark.parser Parser]
           [com.vladsch.flexmark.html HtmlRenderer]
           [com.vladsch.flexmark.util.options MutableDataSet]
           [java.util ArrayList]))

(def options
  (let [options (MutableDataSet.)
        extensions (ArrayList. [])]
    (.set options Parser/EXTENSIONS extensions)
    options))

(def parser (.build (Parser/builder options)))

(def renderer (.build (HtmlRenderer/builder options)))

(defn- render-html [markdown]
  (.render renderer (.parse parser markdown)))

(defn render-markdown [markdown]
  (html/select
    (html/html-resource (java.io.ByteArrayInputStream. (.getBytes (render-html markdown))))
    [:body :> :*]))

