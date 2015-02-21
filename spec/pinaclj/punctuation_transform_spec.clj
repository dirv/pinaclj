(ns pinaclj.punctuation-transform-spec
  (:require [speclj.core :refer :all]
            [pinaclj.punctuation-transform :refer :all]))

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

(describe "transform-quotes"
  (it "starting single quote to &lsquo;"
    (should-contain "&lsquo;singlestart" (transform-quotes single-quotes)))

  (it "inner single quote to &rsquo;"
    (should-contain "singlestart&rsquo;" (transform-quotes single-quotes)))

  (it "inner single quote to &lsquo;"
    (should-contain "&lsquo;singleend" (transform-quotes single-quotes)))

  (it "ending single quote to &rsquo;"
    (should-contain "singleend&rsquo;" (transform-quotes single-quotes)))

  (it "apostrophe to &rsquo;"
    (should-contain "let&rsquo;s" (transform-quotes single-quotes)))

  (it "starting double quote to &lsquo;"
    (should-contain "&ldquo;doublestart" (transform-quotes double-quotes)))

  (it "inner double quote to &rsquo;"
    (should-contain "doublestart&rdquo;" (transform-quotes double-quotes)))

  (it "inner double quote to &lsquo;"
    (should-contain "&ldquo;doubleend" (transform-quotes double-quotes)))

  (it "ending double quote to &rsquo;"
    (should-contain "doubleend&rdquo;" (transform-quotes double-quotes)))

  (it "quotes after punctuation"
    (should-contain ".&rdquo;" (transform-quotes quote-sentence))))

(describe "transform-dashes"
  (it "emdash"
    (should-contain "&emdash;" (transform-dashes "--")))

  (it "endash"
    (should-contain "&endash;" (transform-dashes " - ")))

  (it "hyphen"
    (should-contain "-" (transform-dashes "bleeding-edge"))))

(describe "transform"

  (it "nested tag"
    (should= "‘" (:content (:content (transform nested-tag)))))

  (it "tag list"
    (should= "‘" (:content (second (transform tag-list)))))

  (it "inside html"
    (should= "‘" (:content (transform inside-html))))

  (it "string"
    (should= no-quotes (transform no-quotes)))

  (it "nothing inside code block"
    (should= inside-code-block (transform inside-code-block)))

  (it "nothing inside attributes"
    (should= attributes (transform attributes))))
