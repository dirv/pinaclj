published-at: 2015-08-10T08:02:59.000Z
tags: 100words100days testing
title: Testing, part 1
---
21st-century computer programming is governed by a simple rule: a codebase is only as good as its tests.

I once met a programmer who stated that _good_ programmers do not do testing, because it’s a menial task and therefore a waste of the precious programmer’s ability. I would like to offer two observations. The first is that since I am and always have been a proponent of testing, this programmer was clearly making a judgement about _my_ ability. The comment was passive aggressive in nature. The second and more interesting observation is that testing is being mistaken here for _manual testing_, which is an invention of programmers whose work is so shoddy that they need one or more people &nbsp;running after them to sweep up their mess.

When I talk about the act of “testing”, I do not mean _manual_ testing. Manual testing is a necessary counterpart to automated testing, which is also something that I do not mean when I talk about the act of “testing”. How about I define these terms for you?

*Manual testing*
Human beings (“testers”) simulate real-world users by repeating test scripts over and over again, with the aim of finding bugs in untested code.

*Automated testing*
Writing code versions of manual test scripts so that they can be run by a machine rather than a human, usually on a continuous integration server, after a build.

*Testing*
Writing unit tests before writing any production code; only writing enough production code to make the unit tests pass. Each unit test specify an expected program behaviour.

Lost in these definitions is the question of _who_ performs these jobs? Stay tuned for that answer and more in part 2 tomorrow!