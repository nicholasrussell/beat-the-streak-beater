(ns dev.russell.bts-picker.db.models.roster
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO rosters (player_id, team_id, status, created_at, updated_at)
VALUES(%d, %d, '%s', now(), now())
ON CONFLICT (player_id)
DO UPDATE SET
 team_id = EXCLUDED.team_id,
 status = EXCLUDED.status,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-team-id-query
"
SELECT * FROM rosters WHERE team_id = %d AND status = 'A';
")

(def ^:private delete-by-team-id-query
"
DELETE FROM rosters WHERE team_id = %d;
")

(defn upsert
  [ds roster]
  (jdbc/execute-one! ds
                     [(format upsert-query (:player-id roster) (:team-id roster) (:status roster))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-team-id
  [ds team-id]
  (jdbc/execute-one! ds
                     [(format get-by-team-id-query team-id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn delete-by-team-id
  [ds team-id]
  (jdbc/execute! ds
                 [(format delete-by-team-id-query team-id)]
                 {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
