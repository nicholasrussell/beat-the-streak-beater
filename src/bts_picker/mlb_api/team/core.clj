(ns bts-picker.mlb-api.team.core
  (:require [clojure.set :as set]
            [bts-picker.mlb-api.client.core :as client]
            [clojure.tools.trace :as trace]))

(def ^:private path-teams "/v1/teams")
(def ^:private path-team (str path-teams "/%s"))
(def ^:private path-coaches (str path-team "/coaches"))
(def ^:private path-leaders (str path-team "/leaders"))
(def ^:private path-rosters (str path-team "/roster"))
(def ^:private path-roster (str path-rosters "/%s"))

(defn get-teams
  ([]
   (get-teams {}))
  ([{:keys [sport-ids] :or {sport-ids [1]}}]
   (client/get-stats-api path-teams {:query-params {:sportIds sport-ids}})))

(defn get-team
  ([team-id]
   (get-team team-id))
  ([team-id]
   (client/get-stats-api (format path-team team-id))))

(defn get-coaches
  [team-id]
  (client/get-stats-api (format path-coaches team-id)))

(defn get-leaders
  [team-id]
  (client/get-stats-api (format path-leaders team-id)))

(defn get-roster
  ([team-id]
   (get-roster team-id {}))
  ([team-id {:keys [roster-type]}]
   (client/get-stats-api
     (if roster-type
       (format path-roster team-id roster-type)
       (format path-rosters team-id)))))
