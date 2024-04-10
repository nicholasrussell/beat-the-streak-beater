(ns dev.russell.bts-picker.db.models.league
  (:require [dev.russell.bts-picker.constants :refer [LEAGUE_CODE_AL LEAGUE_CODE_NL]]
            [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO leagues (id, code, name, sport_id, created_at, updated_at)
VALUES(?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 code = EXCLUDED.code,
 name = EXCLUDED.name,
 sport_id = EXCLUDED.sport_id,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM leagues WHERE id = ?;
")

(def ^:private get-by-code-query
  "
SELECT id FROM leagues WHERE code = ?;
")

(def ^:private get-mlb-league-ids-query
  "
SELECT id FROM leagues WHERE code = ? OR code = ?;
")

(defn upsert
  [ds league]
  (db-core/execute-one!
   ds
   [upsert-query (:id league) (:code league) (:name league) (:sport-id league)]))

(defn upsert-batch
  [ds leagues]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [league] [(:id league) (:code league) (:name league) (:sport-id league)]) leagues)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))

(defn get-al-id
  [ds]
  (:id
   (db-core/execute-one!
    ds
    [get-by-code-query LEAGUE_CODE_AL])))

(defn get-nl-id
  [ds]
  (:id
   (db-core/execute-one!
    ds
    [get-by-code-query LEAGUE_CODE_NL])))

(defn get-mlb-league-ids
  [ds]
  (mapv
   :id 
   (db-core/execute!
    ds
    [get-mlb-league-ids-query LEAGUE_CODE_AL LEAGUE_CODE_NL])))
