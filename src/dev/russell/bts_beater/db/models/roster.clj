(ns dev.russell.bts-beater.db.models.roster
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO rosters (player_id, team_id, status, created_at, updated_at)
VALUES(?, ?, ?, now(), now())
ON CONFLICT (player_id)
DO UPDATE SET
 team_id = EXCLUDED.team_id,
 status = EXCLUDED.status,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-team-id-query
  "
SELECT * FROM rosters WHERE team_id = ? AND status = 'A';
")

(def ^:private delete-by-team-id-query
  "
DELETE FROM rosters WHERE team_id = ?;
")

(def ^:private get-by-player-id-query
  "
SELECT * FROM rosters WHERE player_id = ?;
")

(defn upsert
  [ds roster]
  (db-core/execute-one!
   ds
   [upsert-query (:player-id roster) (:team-id roster) (:status roster)]))

(defn upsert-batch
  [ds rosters]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [roster] [(:player-id roster) (:team-id roster) (:status roster)]) rosters)))

(defn get-by-team-id
  [ds team-id]
  (db-core/execute-one!
   ds
   [get-by-team-id-query team-id]))

(defn delete-by-team-id
  [ds team-id]
  (db-core/execute!
   ds
   [delete-by-team-id-query team-id]))

(defn get-by-player-id
  [ds player-id]
  (db-core/execute-one!
   ds
   [get-by-player-id-query player-id]))

