(ns bts-picker.mlb-api.sports.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-sports "/v1/sports")
(def ^:private path-sport (str path-sports "/%s"))

(defn get-sports
  []
  (client/get-stats-api path-sports))

(defn get-sport
  [sport-id]
  (client/get-stats-api (format path-sport sport-id)))
