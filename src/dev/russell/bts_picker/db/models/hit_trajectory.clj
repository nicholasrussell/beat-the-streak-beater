(ns dev.russell.bts-picker.db.models.hit-trajectory
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO hit_trajectories (code, description, created_at, updated_at)
VALUES('%s', '%s', now(), now())
ON CONFLICT (code)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
"
SELECT * FROM hit_trajectories WHERE code = '%s';
")

(defn upsert
  [ds hit-trajectory]
  (jdbc/execute-one! ds
                     [(format upsert-query (:code hit-trajectory) (:description hit-trajectory))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-code
  [ds code]
  (jdbc/execute-one! ds
                     [(format get-by-code-query code)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
