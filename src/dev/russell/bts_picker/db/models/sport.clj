(ns dev.russell.bts-picker.db.models.sport
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [dev.russell.bts-picker.constants :refer [SPORT_CODE_MLB]]))

(def ^:private upsert-query
"
INSERT INTO sports (id, code, name, abbreviation, created_at, updated_at)
VALUES(%d, '%s', '%s', '%s', now(), now())
ON CONFLICT (id)
DO UPDATE SET
 code = EXCLUDED.code,
 name = EXCLUDED.name,
 abbreviation = EXCLUDED.abbreviation,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM sports WHERE id = %d;
")

(def ^:private get-mlb-id-query
(str "
SELECT id FROM sports WHERE code = '"
SPORT_CODE_MLB
"';
"))

(defn upsert
  [ds sport]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id sport) (:code sport) (:name sport) (:abbreviation sport))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-mlb-id
  [ds]
  (:id (jdbc/execute-one! ds
                          [get-mlb-id-query]
                          {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))
