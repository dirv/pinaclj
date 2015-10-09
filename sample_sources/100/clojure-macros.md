published-at: 2015-08-03T07:56:21.000Z
tags: clojure 100words100days
title: Clojure macros
---

The book _On Lisp_ by Paul Graham makes the point that programs are not written _in_ Lisp but _on_ Lisp. The most clever Lisp programs build a domain-specific language (DSL) using the macro system and the codebase is then written in terms of this DSL.

The codebase of a Lisp program written in this way will look very different from any other Lisp codebase. This is unlike object-oriented (OO) programming languages like Java which are designed so that all codebases look the same. Programmers writing object-oriented code desire this because OO codebases tend to become monstrous in size. A million lines here, a million lines there. Functional codebases, on the other hand, are comparably diminutive: a thousand lines here, a thousand lines there.

Clojure, a language variant of Lisp, inherits the very same macro system. However, we are told that the first rule of Clojure macro design is to avoid using macros. I wonder if Paul Graham agrees with this rule? Was this rule written by "post-OO" programmers who wish their Clojure codebases to look the same as their previous OO codebases? Is it simply that their object-oriented heritage has left these programmers with an anti-macro bias?

I have been learning Clojure for the best part of a year, and I've built [a reasonably-sized application](http://www.github.com/dirv/pinaclj) using the language. I'm almost embarrassed to admit that I haven't written a single macro. I am still a post-OO programmer. 

One can make huge strides with Clojure despite knowing only a fractional subset of the language. This is where I feel I am today. I am standing at the ocean shore, looking out across the water. The water is barely covering my ankles.