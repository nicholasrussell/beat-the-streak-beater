(ns dev.russell.bts-picker.db.models.division
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO divisions (id, code, name, sport_id, league_id, created_at, updated_at)
VALUES(%d, '%s', '%s', %d, %d, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 code = EXCLUDED.code,
 name = EXCLUDED.name,
 sport_id = EXCLUDED.sport_id,
 league_id = EXCLUDED.league_id,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM divisions WHERE id = %d;
")

(defn upsert
  [ds division]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id division) (:code division) (:name division) (:sport-id division) (:league-id division))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
