(ns pinaclj.quote-transform-spec
  (:require [speclj.core :refer :all]
            [pinaclj.quote-transform :refer :all]))

(def no-quotes
  "abc")

(def single-quotes
  "'singlestart' 'singleend' let's")

(def double-quotes
  "\"doublestart\" \"doubleend\"")

(def inside-html
  {:tag :p :content "'"})

(def inside-code-block
  {:tag :code :content "'"})

(def attributes
  {:tag :img :attrs {:src "t'est"} :content nil})

(def quote-sentence
  "\"This code is not clean, he said.\"")

(def nested-tag
  {:tag :p :content {:tag :p :content "'"}})

(def tag-list
  '({:tag :p :content "'"} {:tag :p :content "'"}))

(describe "transforms"
  (it "starting single quote to &lsquo;"
    (should-contain "&lsquo;singlestart" (transform-text single-quotes)))

  (it "inner single quote to &rsquo;"
    (should-contain "singlestart&rsquo;" (transform-text single-quotes)))

  (it "inner single quote to &lsquo;"
    (should-contain "&lsquo;singleend" (transform-text single-quotes)))

  (it "ending single quote to &rsquo;"
    (should-contain "singleend&rsquo;" (transform-text single-quotes)))

  (it "apostrophe to &rsquo;"
    (should-contain "let&rsquo;s" (transform-text single-quotes)))

  (it "starting double quote to &lsquo;"
    (should-contain "&ldquo;doublestart" (transform-text double-quotes)))

  (it "inner double quote to &rsquo;"
    (should-contain "doublestart&rdquo;" (transform-text double-quotes)))

  (it "inner double quote to &lsquo;"
    (should-contain "&ldquo;doubleend" (transform-text double-quotes)))

  (it "ending double quote to &rsquo;"
    (should-contain "doubleend&rdquo;" (transform-text double-quotes)))

  (it "inside html"
    (should= "‘" (:content (transform inside-html))))

  (it "quotes after punctuation"
    (should-contain ".&rdquo;" (transform-text quote-sentence)))

  (it "nested tag"
    (should= "‘" (:content (:content (transform nested-tag)))))

  (it "tag list"
    (should= "‘" (:content (second (transform tag-list))))))

(describe "does not transform"

  (it "string with no quotes"
    (should= no-quotes (transform no-quotes)))

  (it "inside code block"
    (should= inside-code-block (transform inside-code-block)))

  (it "attribute quotes"
    (should= attributes (transform attributes))))
