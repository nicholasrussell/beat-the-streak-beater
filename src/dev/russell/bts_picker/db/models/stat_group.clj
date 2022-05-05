(ns dev.russell.bts-picker.db.models.stat-group
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO stat_groups (code, created_at, updated_at)
VALUES('%s', now(), now())
ON CONFLICT (code)
DO UPDATE SET
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
"
SELECT * FROM stat_groups WHERE code = '%s';
")

(defn upsert
  [ds stat-group]
  (jdbc/execute-one! ds
                     [(format upsert-query (:code stat-group))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-code
  [ds code]
  (jdbc/execute-one! ds
                     [(format get-by-code-query code)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
