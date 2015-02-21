(ns pinaclj.punctuation-transform
    (:require [net.cgrand.enlive-html :as html]))

(defn- blank? [ch]
  (or (nil? ch) (Character/isWhitespace ch) (= \> ch)))

(defn- replace-quote [next-char last-char]
  (cond
    (and (= \' next-char) (blank? last-char)) "&lsquo;"
    (= \' next-char) "&rsquo;"
    (and (= \" next-char) (blank? last-char)) "&ldquo;"
    :else "&rdquo;"))

(defn- quote-char? [ch]
  (or (= \' ch) (= \" ch)))

(defn- stream-convert [cur next-char]
  (assoc cur :result (str (:result cur) (if (quote-char? next-char)
                                            (replace-quote next-char (:last-char cur))
                                            next-char))
         :last-char next-char))

(defn- transform-non-code [node string-fn]
  (cond
    (string? node)
      (first (html/html-snippet (string-fn node)))
    (and (map? node) (not (= :code (:tag node))))
      (assoc node :content (transform-non-code (:content node) string-fn))
    (or (seq? node) (vector? node))
      (map #(transform-non-code % string-fn) node)
    :else
      node))

(defn transform-quotes [text]
  (:result (reduce stream-convert {} text)))

(defn transform-dashes [text]
  (-> text
      (clojure.string/replace "--" "&mdash;")
      (clojure.string/replace " - " "&ndash;")))

(defn transform [node]
  (transform-non-code node (comp transform-quotes transform-dashes)))
