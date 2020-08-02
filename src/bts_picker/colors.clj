(ns bts-picker.colors
  (:require [bts-picker.util :as util]
            [clojure.tools.trace :refer :all]
            [bts-picker.config :as config]))

(defn- get-team-color-data
  []
  (util/get-json (config/team-colors-url)))

(defn team-colors
  [team-name]
  (let [color-data (get-team-color-data)
        team-data (first (filter #(= team-name (:name %)) color-data))]
    (if-not (nil? team-data)
      (:hex (:colors team-data))
      [])))
  
