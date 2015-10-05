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
 * **simple to use**: features that support and aid productive and creative writing.

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

        lein publish <posts directory>\first.md

    Take a look at the contents of the file. You'll see that a `published-at` header has been written. This is the cue to Pinaclj that the post is ready for publication. Without this header, Pinaclj will not generate your page.

 5. Run the following command.

        lein generate -s <posts directory> -d <target directory> -t sample_themes/simple

 6. Change to the `gen` directory. You'll find the following files:

        index.html
        first.html
        category/post/index.html

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
 * `published-at` (optional; but the presence of this field determines if the page is published or not. Example: `2015-04-05T12:00:00.000Z`)
 * `url` (optional; if not provided the filename will be used. See below)
 * `tags` (optional; comma-separated)
 * `category` (optional; default is 'post')

You can add any other headers you wish. Headers can be accessed from within your theme's template files.

You can use the `url` header to generate posts as `index.html` files within their own directories, e.g.

    url: my-site/

Will generate the file www.example.com/my-site/index.html.

### Understanding themes and posts

If the name of your post file matches the name of a theme template file, then that post will be rendered with that template. The upshot of this is that you can use your post files to provide data to your templates.

So for example, if you have an `index.md` file with a header of

    blog-title: My Amazing Blog

Then your `index.html` theme template can reference `blog-title` within a `data-id` attribute to retrieve the values.

What's more, each page has an implicit parent and templates can access headers set on parent pages. The default parent hierwachy is that each post's parent is its category page, and each category page has a parent of `index.html`.

The upshot of this is that headers set in your `index.md` post will be accessible to _all_ template files. The same is true for posts belonging to particular categories.

Overriding page values works as you might expect, so a post can override `blog-title` if desired.

You can also explicitly set a page parent by setting the `parent` header. Thr value should be set to the destination page name and not the post file name. So for example:

    parent: index.html
