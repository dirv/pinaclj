published-at: 2015-08-25T14:45:58.000Z
tags: 100words100days,clojure,tdd
title: Clojure print-line debugging, part 2
---
Yesterday I wrote:

> You have a failing test and can’t quite see where the failure lies, so you can insert a few print-lines to show up. Assuming that you find the right suspected points of error and insertion points for your print-lines, you can quickly pin-point the error and fix the code, making your test green.

Unfortunately for us, the initial assumption never holds true. If we knew the point of error, then we wouldn’t need to debug at all. We’d just fix the issue. The print-line debugging technique is _always_ guesswork.

Assume for a second that you’ve been busy reworking a core portion of your codebase. A crucial moment has been reached: a test is failing, and this test exercises code across multiple functions in multiple namespaces.

This is when the desire for print-line debugging kicks in. You already have your test coverage and you know that 99% of the code you need has been written, it’s just a misplaced keyword or two that’s causing you grief.

But there’s another approach to finding these bugs: write more unit tests. Feeling the urge to do print-line debugging is a sign that you need to write some more tests, or at the very least refactor the ones you have. Very often, breaking existing tests down into smaller units will do the trick.