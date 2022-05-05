(ns dev.russell.bts-picker.db.models.metric
  (:require [clojure.string :as string]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO metrics (id, name, unit, stat_group_codes, created_at, updated_at)
VALUES(%d, '%s', '%s', '%s', now(), now())
ON CONFLICT (id)
DO UPDATE SET
 name = EXCLUDED.name,
 unit = EXCLUDED.unit,
 stat_group_codes = EXCLUDED.stat_group_codes,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM metrics WHERE id = %d;
")

(defn- format-stat-group-codes
  [stat-group-codes]
  (str "{" (string/join "," (map #(str "\"" % "\"") (filter some? stat-group-codes))) "}"))

(defn upsert
  [ds metric]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id metric) (:name metric) (:unit metric) (format-stat-group-codes (:stat-group-codes metric)))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
