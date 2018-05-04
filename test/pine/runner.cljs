(ns pine.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [pine.core-test]
            [pine.ring-test]))

(doo-tests 'pine.core-test
           'pine.ring-test)
