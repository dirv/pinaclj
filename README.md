![travis build status](https://api.travis-ci.org/dirv/pinaclj.svg)

# pinaclj

A simple content system for the web. Work in progress.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

You need a theme directory which should have at least two files:

 * `index.html` which is the page list.
 * `post.html` which is the template used for individual posts.

There are example pages here: https://github.com/dirv/pinaclj/tree/master/spec/example_theme

Posts are written in Markdown and should go in a posts directory. Each post begins with a header section. The following are valid headers:

 * `title` (mandatory)
 * `published-at` (optional, but the presence of this field determines if the page is published or not. Example: `2015-04-05T12:00:00.000Z`)
 * `url` (optional; if not provided the filename will be used)
 * `tags` (optional; comma-separated)

You can use the `url` header to generate posts as `index.html` files within their own directories, e.g.

    url: example/

Will generate the file www.yourdomain.com/example/index.html.

Once you have this, you can then run Pinaclj like this:

    lein run -s <pageDir> -d <destinationDir> -t <theme dir>

