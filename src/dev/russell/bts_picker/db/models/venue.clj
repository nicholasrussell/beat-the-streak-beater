(ns dev.russell.bts-picker.db.models.venue
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO venues (id, name, created_at, updated_at)
VALUES(%d, '%s', now(), now())
ON CONFLICT (id)
DO UPDATE SET
 name = EXCLUDED.name,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM venues WHERE id = %d;
")

(defn upsert
  [ds venue]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id venue) (:name venue))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
