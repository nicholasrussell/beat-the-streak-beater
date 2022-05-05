(ns dev.russell.bts-picker.db.models.game-type
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO game_types (id, description, created_at, updated_at)
VALUES('%s', '%s', now(), now())
ON CONFLICT (id)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM game_types WHERE id = '%s';
")

(defn upsert
  [ds game-type]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id game-type) (:description game-type))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
