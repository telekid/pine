# Pine

**__Note:__ Pine is pre-alpha software. Please don't use it in production.**

[![Clojars Project](https://img.shields.io/clojars/v/telekid/pine.svg)](https://clojars.org/telekid/pine)
[![Build Status](https://travis-ci.org/telekid/pine.svg?branch=master)](https://travis-ci.org/telekid/pine)
![Last Released](https://img.shields.io/github/last-commit/google/skia.svg)

For a proof-of-concept re-frame application that makes use of pine, see [Pine App](https://github.com/telekid/pine-app).

## Overview

Pine is a ClojureScript router designed for universal applications.

### Motivation

Traditional routers compare URLs against a set of route definitions, and return a single handler (or keyword) when they find a match.

Pine works a bit differently. Rather than returning a single match result, Pine returns a Clojure set containing the matched route, _along with all of its ancestors_.

Let's look at an example. Here's a code snippet that defines a few routes in Bidi and then matches against them:
```clojure
;; define three routes
(def routes
  ["" {"/users" {"" :users
                 ["/" :id] {"" :user
                            "/delete" :delete-user}}}])
                            
;; match a few routes
user> (match-route "/users" routes)
{:handler :users}

user> (match-route "/users/123" routes)
{:handler :user :route-params {:id 123}}

user> (match-route "/users/123/delete" routes)
{:handler :delete-user :route-params {:id 123}}
```

Note that Bidi returns the `:handler` (a keyword in this case) and its associated `:route-params`.

Pine's `match-route` function returns some additional information:

```clojure
;; define three routes
;; (note: this API is subject to change)
(def routes [{:route-id :users
              :test-path "/users"
              :routes [{:route-id :user
                        :test-path ["/" :id]
                        :routes [{:route-id :delete-user
                                  :test-path "/delete"}]}]}])

;; match a few routes
user> (match-route "/users" routes)
{:active #{:users}}
user> (match-route "/users/123" routes)
{:active #{:users :user} :params {:user {:id 123}}}
user> (match-route "/users" routes)
{:active #{:users :user :delete-user} :params {:user {:id 123}}}
```

Note that `:active` (Pine's equivalent of Bidi's `:handler`) contains a set of all active routes.

### Why is this helpful?

Retaining ancestors makes certain tasks much easier:

- Automatically showing / hiding components in your view tree based on the current route.
- Conditionally styling links based upon the "active" status of their descendents (a la UI Router's [ui-sref-active](https://ui-router.github.io/ng1/docs/0.4.2/#/api/ui.router.state.directive:ui-sref-active) directive.)
- Dispatching leave/retain/enter events when transitioning across route tree states.

...and more! I just haven't had a chance to write them all up yet.


## Additional Features

- Pine supports bi-directional routing. In other words, it can both generate URLs from an active route and its params, and return a set of active routes and params from a given URL.
- Routes are defined in data, not code. In larger applications, it should be convenient to store them in an EDN file.


## Inspiration

Pine borrows heavily from and is inspired by the amazing [Bidi](https://github.com/juxt/bidi) and [UI Router](https://github.com/ui-router/core) libraries.
