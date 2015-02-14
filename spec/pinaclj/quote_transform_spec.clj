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
  "<p>''</p>")

(def inside-code-block
  "<p><code>''\"\"</code></p>")

(def attributes
  "<img src=\"t'est\" />")

(def quote-sentence
  "\"This code is not clean, he said.\"")

(def code-with-class
  "<p><code class=\"clojure\">''</code></p>")

(describe "transforms"
  (it "starting single quote to &lsquo;"
    (should-contain "&lsquo;singlestart" (transform single-quotes)))

  (it "inner single quote to &rsquo;"
    (should-contain "singlestart&rsquo;" (transform single-quotes)))

  (it "inner single quote to &lsquo;"
    (should-contain "&lsquo;singleend" (transform single-quotes)))

  (it "ending single quote to &rsquo;"
    (should-contain "singleend&rsquo;" (transform single-quotes)))

  (it "apostrophe to &rsquo;"
    (should-contain "let&rsquo;s" (transform single-quotes)))

  (it "starting double quote to &lsquo;"
    (should-contain "&ldquo;doublestart" (transform double-quotes)))

  (it "inner double quote to &rsquo;"
    (should-contain "doublestart&rdquo;" (transform double-quotes)))

  (it "inner double quote to &lsquo;"
    (should-contain "&ldquo;doubleend" (transform double-quotes)))

  (it "ending double quote to &rsquo;"
    (should-contain "doubleend&rdquo;" (transform double-quotes)))

  (it "inside html"
    (should= "<p>&lsquo;&rsquo;</p>" (transform inside-html)))

  (it "quotes after punctuation"
    (should-contain ".&rdquo;" (transform quote-sentence))))

(describe "does not transform"

  (it "string with no quotes"
    (should= no-quotes (transform no-quotes)))

  (it "inside code element with class block"
    (should= code-with-class (transform code-with-class)))

  (it "inside code block"
    (should= inside-code-block (transform inside-code-block)))

  (it "attribute quotes"
    (should= attributes (transform attributes))))
