(ns dev.russell.bts-picker.db.models.roster-type
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO roster_types (parameter, description, lookup_name, created_at, updated_at)
VALUES(?, ?, ?, now(), now())
ON CONFLICT (parameter)
DO UPDATE SET
 parameter = EXCLUDED.parameter,
 description = EXCLUDED.description,
 lookup_name = EXCLUDED.lookup_name,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-parameter-query
  "
SELECT * FROM roster_types WHERE parameter = ?;
")

(defn upsert
  [ds roster-type]
  (db-core/execute-one!
   ds
   [upsert-query (:parameter roster-type) (:description roster-type) (:lookup-name roster-type)]))

(defn upsert-batch
  [ds roster-types]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [roster-type] [(:parameter roster-type) (:description roster-type) (:lookup-name roster-type)]) roster-types)))

(defn get-by-parameter
  [ds parameter]
  (db-core/execute-one!
   ds
   [get-by-parameter-query parameter]))
