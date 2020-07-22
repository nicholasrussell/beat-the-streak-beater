(ns bts-picker.mlb-api.league.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-leagues "/v1/league")
(def ^:private path-league (str path-leagues "/%s"))

(defn get-leagues
  []
  (client/get path-leagues))

(defn get-league
  [league-id]
  (client/get (format path-league league-id)))

