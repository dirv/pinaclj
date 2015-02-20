(ns pinaclj.quote-transform
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

(defn transform-text [text]
  (:result (reduce stream-convert {} text)))

(defn transform [node]
  (cond
    (string? node) (first (html/html-snippet (transform-text node)))
    (and (map? node) (not (= :code (:tag node)))) (assoc node :content (transform (:content node)))
    (seq? node) (map transform node)
    :else node))
