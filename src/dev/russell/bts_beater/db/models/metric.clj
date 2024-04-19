(ns dev.russell.bts-beater.db.models.metric
  (:require [clojure.string :as string]
            [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO metrics (id, name, unit, stat_group_codes, created_at, updated_at)
VALUES(?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 name = EXCLUDED.name,
 unit = EXCLUDED.unit,
 stat_group_codes = EXCLUDED.stat_group_codes,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM metrics WHERE id = ?;
")

(defn- format-stat-group-codes
  [stat-group-codes]
  (str "{" (string/join "," (map #(str "\"" % "\"") (filter some? stat-group-codes))) "}"))

(defn upsert
  [ds metric]
  (db-core/execute-one!
   ds
   [upsert-query (:id metric) (:name metric) (:unit metric) (into-array String (:stat-group-codes metric))]))

(defn upsert-batch
  [ds metrics]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [metric] [(:id metric) (:name metric) (:unit metric) (into-array String (:stat-group-codes metric))]) metrics)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))
