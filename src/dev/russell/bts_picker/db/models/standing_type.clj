(ns dev.russell.bts-picker.db.models.standing-type
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO standing_types (name, description, created_at, updated_at)
VALUES('%s', '%s', now(), now())
ON CONFLICT (name)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-name-query
"
SELECT * FROM standing_types WHERE name = '%s';
")

(defn upsert
  [ds standing-type]
  (jdbc/execute-one! ds
                     [(format upsert-query (:name standing-type) (:description standing-type))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-name
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-name-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
