(ns dev.russell.bts-picker.db.models.standing-type
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO standing_types (name, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (name)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-name-query
  "
SELECT * FROM standing_types WHERE name = ?;
")

(defn upsert
  [ds standing-type]
  (db-core/execute-one!
   ds
   [upsert-query (:name standing-type) (:description standing-type)]))

(defn upsert-batch
  [ds standing-types]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [standing-type] [(:name standing-type) (:description standing-type)]) standing-types)))

(defn get-by-name
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-name-query id]))
