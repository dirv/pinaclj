published-at: 2015-08-19T12:18:01.000Z
tags: 100words100days,exceptions,code,programming,chevron
title: Exceptions
---

<p>I believe I could write a book about exceptions, except that I doubt it would be accepted for publication. What follows is an excerpt from that book.</p>
<p>It astounds me how easy it is to get exception handling wrong.&nbsp;Exceptions give us a clean way to handle failures. Programming languages with exception support segregate normal program flow and error handling so that failure conditions do not pollute the coder&rsquo;s landscape.</p>
<p>My first job involved working on a code base that did not use exceptions, preferring instead to use function return values to indicate success or failure. This is a very normal programming practice in C, and it results in code looking like this:</p>
<pre><code>
RESULT result = func1(...);
if(result == RESULT_OK) {
    result = func2(...);
    if(result == RESULT_OK) {
        result = func3(...);
        if (result == RESULT_OK) {
            ...
        }
    } else {
    ...
    }
} else {
...
}</code></pre>

<p>You&rsquo;ll notice that all this indirection makes the code look like a big chevron (the symbol &lsquo;&gt;&rsquo;). This pattern was repeated all over the codebase. I remember my boss being labelled &ldquo;the chevron king&rdquo; because he had one code file that ran to 13 levels of indentation. He was very proud of the accolade.</p>