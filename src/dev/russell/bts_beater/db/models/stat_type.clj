(ns dev.russell.bts-beater.db.models.stat-type
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO stat_types (code, created_at, updated_at)
VALUES(?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM stat_types WHERE code = ?;
")

(defn upsert
  [ds stat-type]
  (db-core/execute-one!
   ds
   [upsert-query (:code stat-type)]))

(defn upsert-batch
  [ds stat-types]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [stat-type] [(:code stat-type)]) stat-types)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
