(ns dev.russell.bts-picker.db.models.wind
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO winds (code, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM winds WHERE code = ?;
")

(defn upsert
  [ds wind]
  (db-core/execute-one!
   ds
   [upsert-query (:code wind) (:description wind)]))

(defn upsert-batch
  [ds winds]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [wind] [(:code wind) (:description wind)]) winds)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
