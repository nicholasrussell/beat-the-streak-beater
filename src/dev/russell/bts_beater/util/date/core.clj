(ns dev.russell.bts-beater.util.date.core
  (:require [java-time])
  (:import (java.time LocalDate)))

(defn- ->int
  [i]
  (if (string? i)
    (Integer/parseInt i)
    i))

(defn now
  []
  (java-time/local-date))

(defn of
  [year month day]
  (java-time/local-date (->int year) (->int month) (->int day)))

(defn parse-date
  [date-str]
  (LocalDate/parse date-str))

(defn format-date
  [date]
  (cond
    (instance? LocalDate date) (java-time/format date)
    (string? date) date
    :else nil))

