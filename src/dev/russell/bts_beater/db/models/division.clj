(ns dev.russell.bts-beater.db.models.division
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO divisions (id, code, name, sport_id, league_id, created_at, updated_at)
VALUES(?, ?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 code = EXCLUDED.code,
 name = EXCLUDED.name,
 sport_id = EXCLUDED.sport_id,
 league_id = EXCLUDED.league_id,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM divisions WHERE id = ?;
")

(defn upsert
  [ds division]
  (db-core/execute-one!
   ds
   [upsert-query (:id division) (:code division) (:name division) (:sport-id division) (:league-id division)]))

(defn upsert-batch
  [ds divisions]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [division] [(:id division) (:code division) (:name division) (:sport-id division) (:league-id division)]) divisions)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))
