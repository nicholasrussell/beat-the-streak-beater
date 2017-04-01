(ns bts-picker.colors
  (:require [clojure.string :as str]
            [clj-http.client :as http-client]
            [cheshire.core :as cheshire]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.tools.trace :refer :all]))

(def ^:private colors-url "https://raw.githubusercontent.com/jimniels/teamcolors/master/static/data/teams.json")

(defn- get-team-color-data
  []
  (keywordize-keys (cheshire/parse-string (:body (http-client/get colors-url)))))

(defn team-colors
  [team-name]
  (let [color-data (get-team-color-data)
        team-data (first (filter #(= team-name (:name %)) color-data))]
    (if-not (nil? team-data)
      (:hex (:colors team-data))
      [])))
  
