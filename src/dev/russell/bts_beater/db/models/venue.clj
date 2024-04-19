(ns dev.russell.bts-beater.db.models.venue
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO venues (id, name, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 name = EXCLUDED.name,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM venues WHERE id = ?;
")

(defn upsert
  [ds venue]
  (db-core/execute-one!
   ds
   [upsert-query (:id venue) (:name venue)]))

(defn upsert-batch
  [ds venues]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [venue] [(:id venue) (:name venue)]) venues)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))
