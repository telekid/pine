# Running a development repl

in Spacemacs, `,sI` will start a Clojure repl and a CLJS repl.
When prompted, choose Figwheel as the repl type.


# Testing pine

To run Clojure tests, run `lein test` at the command line.

To run ClojureScript tests, run `lein test-cljs` at the command line.
Note: You may need to run `npm install -g karma-cli`


# Releasing pine

Creating a new version of pine is a two step process.

First, start by creating a new release by running `lein release`.

(Note: You must have at least one commit since your most recent tagged commit.)

Then, deploy pine to Clojars with `lein deploy clojars`.
