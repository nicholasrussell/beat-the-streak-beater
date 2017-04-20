(ns bts-picker.util
  (:require [java-time :as t]
            [clj-http.client :as http-client]
            [cheshire.core :as cheshire]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.string :as str]
            [clojure.tools.trace :refer :all]))

(defn- date->parts
  [date]
  (let [[year month day] (str/split date #"\-")]
    {:year year :month month :day day}))

(defn date->year
  [date]
  (:year (date->parts date)))

(defn date->month
  [date]
  (:month (date->parts date)))

(defn date->day
  [date]
  (:day (date->parts date)))

(defn- date->iso-date
  [date]
  (t/format "yyyy-MM-dd" date))

(defn- iso-date->date
  [date]
  (t/local-date "yyyy-MM-dd" date))

(defn date->mlb-date
  [date]
  (str/replace date #"\-" "/"))

(defn n-days-ago
  [date n]
  (date->iso-date (t/minus (iso-date->date date) (t/days n))))

(defn now
  []
  (date->iso-date (t/local-date)))

(defn get-json
  [url]
  (keywordize-keys (cheshire/parse-string (:body (http-client/get url)))))

