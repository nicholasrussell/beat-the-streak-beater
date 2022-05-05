(ns dev.russell.bts-picker.db.models.stat-type
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO stat_types (code, created_at, updated_at)
VALUES('%s', now(), now())
ON CONFLICT (code)
DO UPDATE SET
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
"
SELECT * FROM stat_types WHERE code = '%s';
")

(defn upsert
  [ds stat-type]
  (jdbc/execute-one! ds
                     [(format upsert-query (:code stat-type))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-code
  [ds code]
  (jdbc/execute-one! ds
                     [(format get-by-code-query code)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
