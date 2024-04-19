(ns dev.russell.bts-beater.db.models.pitch-type
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO pitch_types (code, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM pitch_types WHERE code = ?;
")

(defn upsert
  [ds pitch-type]
  (db-core/execute-one!
   ds
   [upsert-query (:code pitch-type) (:description pitch-type)]))

(defn upsert-batch
  [ds pitch-types]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [pitch-type] [(:code pitch-type) (:description pitch-type)]) pitch-types)))


(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
