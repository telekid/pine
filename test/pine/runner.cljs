(ns pine.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [pine.core-test]))

(doo-tests 'pine.core-test)
