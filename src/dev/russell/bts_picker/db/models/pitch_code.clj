(ns dev.russell.bts-picker.db.models.pitch-code
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO pitch_codes (code, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM pitch_codes WHERE code = ?;
")

(defn upsert
  [ds pitch-code]
  (db-core/execute-one!
   ds
   [upsert-query (:code pitch-code) (:description pitch-code)]))

(defn upsert-batch
  [ds pitch-codes]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [pitch-code] [(:code pitch-code) (:description pitch-code)]) pitch-codes)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
