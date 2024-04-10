(ns dev.russell.bts-picker.db.models.hit-trajectory
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO hit_trajectories (code, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at
RETURNING *;
")

(def ^:private get-by-code-query
  "
SELECT * FROM hit_trajectories WHERE code = ?;
")

(defn upsert
  [ds hit-trajectory]
  (db-core/execute-one!
   ds
   [upsert-query (:code hit-trajectory) (:description hit-trajectory)]))

(defn upsert-batch
  [ds hit-trajectories]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [hit-trajectory] [(:code hit-trajectory) (:description hit-trajectory)]) hit-trajectories)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
