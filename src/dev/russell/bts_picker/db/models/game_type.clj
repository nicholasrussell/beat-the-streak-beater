(ns dev.russell.bts-picker.db.models.game-type
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO game_types (id, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM game_types WHERE id = ?;
")

(defn upsert
  [ds game-type]
  (db-core/execute-one!
   ds
   [upsert-query (:id game-type) (:description game-type)]))

(defn upsert-batch
  [ds game-types]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [game-type] [(:id game-type) (:description game-type)]) game-types)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))
