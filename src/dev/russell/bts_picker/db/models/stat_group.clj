(ns dev.russell.bts-picker.db.models.stat-group
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO stat_groups (code, created_at, updated_at)
VALUES(?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM stat_groups WHERE code = ?;
")

(defn upsert
  [ds stat-group]
  (db-core/execute-one!
   ds
   [upsert-query (:code stat-group)]))

(defn upsert-batch
  [ds stat-groups]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [stat-group] [(:code stat-group)]) stat-groups)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
