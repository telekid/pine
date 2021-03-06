# Running a development repl

in Spacemacs, `,sI` will start a Clojure repl and a CLJS repl.
When prompted, choose Figwheel as the repl type.


# Testing pine

To run Clojure tests, run `lein test` at the command line.

To run ClojureScript tests, run `lein test-cljs` at the command line.
Note: You may need to run `lein npm install` first.


# Releasing pine

Creating a new version of pine is a two step process.

## Create a release
First, start by creating a new release by running `lein release [type]`, where
`[type]` is one of the directives indicated in the [lein-v
documentation](https://github.com/roomkey/lein-v#support-for-lein-release).

For example, you might run `lein release snapshot`.

This will automatically generate a new git tag based upon the rules defined in
the lein-v docs, and then push that tag to `remote`.

(Note: You must have at least one commit since your most recent tagged commit.
If you don't, `lein-v` will throw an unhelpful error.)

## Deploy to clojars
Then, deploy pine to Clojars with `lein deploy clojars`.

## Troubleshooting
- Make sure you've defined `git config --global user.signingkey YOURKEY!!`
- Ensure you've specified `{:user {:signing {:gpg-key "YOURKEY!!"}}}` in `~/.lein/profiles.clj`
- If you see an error like `Could not transfer metadata [...] ReasonPhrase: Forbidden - the version in the pom (0.1.3-DIRTY) does not match the version you are deploying to (0.1.3).`, try deleting `pom.xml` and releasing again
