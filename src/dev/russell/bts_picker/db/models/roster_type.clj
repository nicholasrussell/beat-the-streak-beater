(ns dev.russell.bts-picker.db.models.roster-type
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO roster_types (parameter, description, lookup_name, created_at, updated_at)
VALUES('%s', '%s', '%s', now(), now())
ON CONFLICT (parameter)
DO UPDATE SET
 parameter = EXCLUDED.parameter,
 description = EXCLUDED.description,
 lookup_name = EXCLUDED.lookup_name,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-parameter-query
"
SELECT * FROM roster_types WHERE parameter = '%s';
")

(defn upsert
  [ds roster-type]
  (jdbc/execute-one! ds
                     [(format upsert-query (:parameter roster-type) (:description roster-type) (:lookup-name roster-type))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-parameter
  [ds parameter]
  (jdbc/execute-one! ds
                     [(format get-by-parameter-query parameter)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
