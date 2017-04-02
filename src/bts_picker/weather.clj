(ns bts-picker.weather
  (:require [bts-picker.util :as util]
            [clojure.string :as str]
            [clojure.tools.trace :refer :all]))

;; TODO move me
(def ^:private api-key "***REMOVED***")

(def ^:private base-url (str "***REMOVED***" api-key))

(defn- strip-state
  [location]
  (first (str/split location #",")))

(defn- weather-url
  [location]
  (format base-url (strip-state location)))

(defn- weather-data
  [location]
  (util/get-json (weather-url location)))

(defn- get-weather-data
  [location]
  (let [data (weather-data location)]
    {:description (:description (first (:weather data)))
     :current-temp (:temp (:main data))
     :low-temp (:temp_min (:main data))
     :high-temp (:temp_max (:main data))
     :wind (:wind data)}))

(defn weather-for-location
  [location]
  (if (str/blank? location)
    {}
    (get-weather-data location)))
