# Browser Routing

## re-frame Integration

Pine comes with a collection of re-frame subscriptions, events and components to
help you get started building a URL-driven application.

Integrating Pine with re-frame is a two step process.

### Step 1: Register your routes

First, import `pine.re-frame`. This will register Pine's re-frame subscriptions
and event handlers.

Then, import your route definitions and pass them to `pine.router/set-routes!`.
This will make your route structure available to `pine.re-frame`.

```clojure
(ns your.app
  (:require [your.app.routes :refer [routes]]
            [pine.router :refer [set-routes!]
            [pine.re-frame]])) ;; <- register subscriptions and event handlers

(set-routes! routes)
```

### Step 2: Connect to HTML5 History API

Once you have Pine's re-frame integration installed and you've registered your
routes, you should configure your application to dispatch
`:pine/handle-url-change` whenever the user navigates using the browser's
built-in navigation functionality. This ensure that your re-frame DB's
`:pine/location` is always up-to-date.

Finally, you'll need to install a coeffect to update the browser's URL state
whenever users trigger navigation events within your application. To do that,
register a coeffect called `:pine/update-url`. The associated function should
update the URL.

To see a concrete example of Step 2, read the next section.


## Accountant Integration

[Accountant](https://github.com/venantius/accountant) is a small utility library
that provides a simple interface to the HTML5 History API. It pairs nicely with
Pine and re-frame, and can be used to automatically update Pine's location state
as users navigate around your page.

Integrating Pine with Accountant is a two step process. First, configure
Accountant to automatically dispatch a `:pine/handle-url-change` event whenever
the user navigates:

```clojure
(ns your.app
  (:require [accountant.core :as accountant]
            [pine.router :refer [match-route]]
            [re-frame.core :refer [dispatch]]))

(accountant/configure-navigation!
 {:nav-handler #(dispatch [:pine/handle-url-change %])
  :path-exists? #(boolean (match-route %))})
```

Then, register a re-frame coeffect to update the URL whenever `:pine/update-url`
events are dispatched:

```clojure
(re-frame/reg-fx
 :pine/update-url
 (fn [url]
   (accountant/navigate! url)))
```

## Using Pine with re-frame

### Navigation

TODO: Write this section
- Dispatch `:pine/navigate`

### Leave/Retain/Enter Events

# Server Routing
## Ring integration

TODO: Write this section



