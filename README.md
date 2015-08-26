![travis build status](https://api.travis-ci.org/dirv/pinaclj.svg)

# Pinaclj (pronounced "pinnacle")

Pinaclj is a simple content system for the web written in Clojure, currently under active development.

**Note**: This is still early-stage software but explorers are welcome.

In its current state it is a capable static website generator that takes a set of Markdown files together with a set of templates (a theme) and transforms it into your generated site.

Current features:

 * File-based YAML source files
 * Markdown support
 * HTML5 data attribute based templating
 * Incremental builds
 * Page categories
 * Tags
 * RSS feeds
 * Punctuation transformations
 * Extensible template functions

## Goals

Pinaclj's aims are to be:

 * **beautifully minimal**: this is reflected in the choice of language (Clojure) and the choice of templating engine (Enlive).
 * **simple to design**: you can create themes with just basic HTML. No programming knowledge is required.
 * **simple to use**: features that support and aid productive and creative writing

## Upcoming features

The [GitHub issue tracker](https://github.com/dirv/pinaclj/issues) lists all features currently planned for the first release, but here's a short summary:

 * Support for media
 * Post series
 * Moving and deleting pages
 * i18n

## Future vision

 * Git integration
 * Role-based, multi-user support with workflow
 * Web UI with a focus on aiding writing
 * iPhone and Android apps

## Getting involved

I welcome contributions of all types. The [issues list](https://github.com/dirv/pinaclj/issues) is up-to-date: pull requests are welcome.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Building your first post

Right now you'll need to run from source.

 1. Clone the repository using `https://github.com/dirv/pinaclj.git`.
 2. Create a `gen` directory where your site will be generated in.
 2. Create a `posts` directory where you want your posts to be stored. This could be another Git repo if you wish.
 3. Within the `posts` directory, create a new file called `first.md` with the following content:

        title: First post
        ---
        Hello, world!

 4. Change to the `pinaclj` directory and run the following command.
 
        lein publish <posts folder>\first.md
    
    Take a look at the contents of the file. You'll see that a `published-at` header has been written. This is the cue to Pinaclj that the post is ready for publication. Without this header, Pinaclj will not generate your page.

 5. Run the following command.

        lein generate -s <posts folder> -d <target folder> -t sample_themes/simple

 6. Change to the `gen` directory. You'll find the following files:

        index.html
        first.html
        category/uncategorized/index.html

 7. Explore the content of the files and compare to how it matches up with the theme located in `sample_themes/simple`.

Your next step, most likely, will be to build your own theme.

### Themes

A theme directory should have at least two files:

 * `index.html` which is the page list.
 * `post.html` which is the template used for individual posts.

There are example pages here: https://github.com/dirv/pinaclj/tree/master/sample_themes

### Posts

Posts are written in Markdown. Each post begins with a header section. The following are valid headers:

 * `title` (mandatory)
 * `published-at` (optional, but the presence of this field determines if the page is published or not. Example: `2015-04-05T12:00:00.000Z`)
 * `url` (optional; if not provided the filename will be used. See below)
 * `tags` (optional; comma-separated)
 * `category` (optional; default is 'uncategorized')

You can add any other headers you wish. Headers can be accessed from within your theme's template files.

You can use the `url` header to generate posts as `index.html` files within their own directories, e.g.

    url: my-site/

Will generate the file www.example.com/my-site/index.html.
