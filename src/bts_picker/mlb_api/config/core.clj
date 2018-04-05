(ns bts-picker.mlb-api.config.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-game-statuses "/v1/gameStatus")
(def ^:private path-game-types "/v1/gameTypes")
(def ^:private path-league-leader-types "/v1/leagueLeaderTypes")
(def ^:private path-metrics "/v1/metrics")
(def ^:private path-positions "/v1/positions")
(def ^:private path-roster-types "/v1/rosterTypes")
(def ^:private path-schedule-event-types "/v1/scheduleEventTypes")
(def ^:private path-situation-codes "/v1/situationCodes")
(def ^:private path-standings-types "/v1/standingsTypes")
(def ^:private path-stat-groups "/v1/statGroups")
(def ^:private path-stat-types "/v1/statTypes")

(defn get-game-statuses
  []
  (client/get-stats-api path-game-statuses))

(defn get-game-types
  []
  (client/get-stats-api path-game-types))

(defn get-league-leader-types
  []
  (client/get-stats-api path-league-leader-types))

(defn get-metrics
  []
  (client/get-stats-api path-metrics))

(defn get-positions
  []
  (client/get-stats-api path-positions))

(defn get-roster-types
  []
  (client/get-stats-api path-roster-types))

(defn get-schedule-event-types
  []
  (client/get-stats-api path-schedule-event-types))

(defn get-situation-codes
  []
  (client/get-stats-api path-situation-codes))

(defn get-standings-types
  []
  (client/get-stats-api path-standings-types))

(defn get-stat-groups
  []
  (client/get-stats-api path-stat-groups))

(defn get-stat-types
  []
  (client/get-stats-api path-stat-types))
