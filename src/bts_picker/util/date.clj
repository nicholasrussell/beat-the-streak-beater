(ns bts-picker.util.date
  (:require [java-time])
  (:import (java.time LocalDate)))

(defn now
  []
  (java-time/local-date))

(defn of
  [year month day]
  (java-time/local-date year month day))

(defn format-date
  [date]
  (cond
    (instance? LocalDate date) (java-time/format date)
    (string? date) date
    :else nil))