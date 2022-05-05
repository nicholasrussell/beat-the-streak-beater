(ns dev.russell.bts-picker.util.date-test
  (:require [clojure.test :as t]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.spec.test.alpha :as stest]
            [com.gfredericks.test.chuck :as chuck]
            [dev.russell.bts-picker.util.date.core :as impl]
            [dev.russell.bts-picker.util.date.spec :as spec])
  (:import (java.time LocalDate)
           (java.time.format DateTimeFormatter)))

(def trials (chuck/times 100))

(defspec ->int-test
         trials
         (prop/for-all [i gen/int]
                       (let [fn #'impl/->int]
                         (= i (fn i) (fn (str i))))))

(defspec now-test
         trials
         (prop/for-all []
                       (= (instance? LocalDate (impl/now)))))

(defspec of-test
         trials
         (prop/for-all [[year month day] spec/local-date-parts-generator]
                       (= (LocalDate/of (#'impl/->int year) (#'impl/->int month) (#'impl/->int day))
                          (impl/of year month day))))

(defspec format-date-test
         trials
         (prop/for-all [local-date spec/local-date-generator]
                       (= local-date
                          (LocalDate/parse (impl/format-date local-date) DateTimeFormatter/ISO_LOCAL_DATE))))

(stest/summarize-results (stest/check `impl/now))
(stest/summarize-results (stest/check `impl/of))
(stest/summarize-results (stest/check `impl/format-date))
