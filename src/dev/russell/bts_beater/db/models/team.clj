(ns dev.russell.bts-beater.db.models.team
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO teams (id, name, venue_id, team_code, abbreviation, team_name, location_name, league_id, division_id, created_at, updated_at)
VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 name = EXCLUDED.name,
 venue_id = EXCLUDED.venue_id,
 team_code = EXCLUDED.team_code,
 abbreviation = EXCLUDED.abbreviation,
 team_name = EXCLUDED.team_name,
 location_name = EXCLUDED.location_name,
 league_id = EXCLUDED.league_id,
 division_id = EXCLUDED.division_id,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM teams WHERE id = ?;
")

(def ^:private get-all-query
  "
SELECT * FROM teams;
")

(defn upsert
  [ds team]
  (db-core/execute-one!
   ds
   [upsert-query (:id team) (:name team) (:venue-id team) (:team-code team) (:abbreviation team) (:team-name team) (:location-name team) (:league-id team) (:division-id team)]))

(defn upsert-batch
  [ds teams]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [team] [(:id team) (:name team) (:venue-id team) (:team-code team) (:abbreviation team) (:team-name team) (:location-name team) (:league-id team) (:division-id team)]) teams)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))

(defn get-all
  [ds]
  (db-core/execute!
   ds
   [get-all-query]))
