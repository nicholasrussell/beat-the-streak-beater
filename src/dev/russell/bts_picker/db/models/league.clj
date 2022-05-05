(ns dev.russell.bts-picker.db.models.league
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [dev.russell.bts-picker.constants :refer [LEAGUE_CODE_AL LEAGUE_CODE_NL]]))

(def ^:private upsert-query
"
INSERT INTO leagues (id, code, name, sport_id, created_at, updated_at)
VALUES(%d, '%s', '%s', %d, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 code = EXCLUDED.code,
 name = EXCLUDED.name,
 sport_id = EXCLUDED.sport_id,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM leagues WHERE id = %d;
")

(def ^:private get-al-id-query
(str "
SELECT id FROM leagues WHERE code = '"
LEAGUE_CODE_AL
"';
"))

(def ^:private get-nl-id-query
(str "
SELECT id FROM leagues WHERE code = '"
LEAGUE_CODE_NL
"';
"))

(def ^:private get-mlb-league-ids-query
(str "
SELECT id FROM leagues WHERE code = '"
LEAGUE_CODE_AL
" OR code ="
LEAGUE_CODE_NL
"';
"))

(defn upsert
  [ds league]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id league) (:code league) (:name league) (:sport-id league))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-al-id
  [ds]
  (:id (jdbc/execute-one! ds
                          [get-al-id-query]
                          {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))

(defn get-nl-id
  [ds]
  (:id (jdbc/execute-one! ds
                          [get-nl-id-query]
                          {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))

(defn get-mlb-league-ids
  [ds]
  (mapv
   :id 
   (jdbc/execute! ds
                  [get-mlb-league-ids-query]
                  {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))
