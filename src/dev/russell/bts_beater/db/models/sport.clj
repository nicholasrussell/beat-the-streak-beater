(ns dev.russell.bts-beater.db.models.sport
  (:require [dev.russell.bts-beater.constants :refer [SPORT_CODE_MLB]]
            [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO sports (id, code, name, abbreviation, created_at, updated_at)
VALUES(?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 code = EXCLUDED.code,
 name = EXCLUDED.name,
 abbreviation = EXCLUDED.abbreviation,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM sports WHERE id = ?;
")

(def ^:private get-mlb-id-query
  "
SELECT id FROM sports WHERE code = ?;
")

(defn upsert
  [ds sport]
  (db-core/execute-one!
   ds
   [upsert-query (:id sport) (:code sport) (:name sport) (:abbreviation sport)]))

(defn upsert-batch
  [ds sports]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [sport] [(:id sport) (:code sport) (:name sport) (:abbreviation sport)]) sports)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))

(defn get-mlb-id
  [ds]
  (:id
   (db-core/execute-one!
    ds
    [get-mlb-id-query SPORT_CODE_MLB])))
