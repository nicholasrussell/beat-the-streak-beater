(ns dev.russell.bts-picker.db.models.team
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO teams (id, name, venue_id, team_code, abbreviation, team_name, location_name, league_id, division_id, created_at, updated_at)
VALUES(%d, '%s', %d, '%s', '%s', '%s', '%s', %d, %d, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 name = EXCLUDED.name,
 venue_id = EXCLUDED.venue_id,
 team_code = EXCLUDED.team_code,
 abbreviation = EXCLUDED.abbreviation,
 team_name = EXCLUDED.team_name,
 location_name = EXCLUDED.location_name,
 league_id = EXCLUDED.league_id,
 division_id = EXCLUDED.division_id,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
"
SELECT * FROM teams WHERE id = '%d';
")

(def ^:private get-all-query
"
SELECT * FROM teams;
")

(defn upsert
  [ds team]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id team) (:name team) (:venue-id team) (:team-code team) (:abbreviation team) (:team-name team) (:location-name team) (:league-id team) (:division-id team))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-all
  [ds]
  (jdbc/execute! ds
                 [get-all-query]
                 {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
