(ns pinaclj.quote-transform-spec
  (:require [speclj.core :refer :all]
            [pinaclj.quote-transform :refer :all]))

(def noQuotes
  "abc")

(def singleQuotes
  "'singlestart' 'singleend' let's")

(def doubleQuotes
  "\"doublestart\" \"doubleend\"")

(def insideHtml
  "<p>''</p>")

(describe "replace quotes"
  (it "returns string with no quotes"
    (should= noQuotes (convert-quote-text noQuotes)))

  (it "converts starting single quote to &lsquo;"
    (should-contain "&lsquo;singlestart" (convert-quote-text singleQuotes)))

  (it "converts inner single quote to &rsquo;"
    (should-contain "singlestart&rsquo;" (convert-quote-text singleQuotes)))

  (it "converts inner single quote to &lsquo;"
    (should-contain "&lsquo;singleend" (convert-quote-text singleQuotes)))

  (it "converts ending single quote to &rsquo;"
    (should-contain "singleend&rsquo;" (convert-quote-text singleQuotes)))

  (it "converts apostrophe to &rsquo;"
    (should-contain "let&rsquo;s" (convert-quote-text singleQuotes)))

  (it "converts starting double quote to &lsquo;"
    (should-contain "&ldquo;doublestart" (convert-quote-text doubleQuotes)))

  (it "converts inner double quote to &rsquo;"
    (should-contain "doublestart&rdquo;" (convert-quote-text doubleQuotes)))

  (it "converts inner double quote to &lsquo;"
    (should-contain "&ldquo;doubleend" (convert-quote-text doubleQuotes)))

  (it "converts ending double quote to &rsquo;" (should-contain "doubleend&rdquo;" (convert-quote-text doubleQuotes)))

  (it "converts inside html"
    (should= "<p>&lsquo;&rsquo;</p>" (convert-quote-text insideHtml))))
