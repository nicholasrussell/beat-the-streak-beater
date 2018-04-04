(ns bts-picker.mlb-api.league.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-leagues "/v1/leagues")
(def ^:private path-league (str path-leagues "/%s"))

(defn get-league
  [league-id & {:keys []}]
  (client/get (format path-league league-id)))
