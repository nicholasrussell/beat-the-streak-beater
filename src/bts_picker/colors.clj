(ns bts-picker.colors
  (:require [bts-picker.util :as util]
            [clojure.tools.trace :refer :all]))

(def ^:private colors-url "https://raw.githubusercontent.com/jimniels/teamcolors/master/static/data/teams.json")

(defn- get-team-color-data
  []
  (util/get-json colors-url))

(defn team-colors
  [team-name]
  (let [color-data (get-team-color-data)
        team-data (first (filter #(= team-name (:name %)) color-data))]
    (if-not (nil? team-data)
      (:hex (:colors team-data))
      [])))
  
