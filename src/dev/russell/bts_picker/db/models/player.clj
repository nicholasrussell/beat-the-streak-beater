(ns dev.russell.bts-picker.db.models.player
  (:require [clojure.string :as string]
            [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO players (id, first_name, last_name, full_name, primary_number, birth_date, weight, active, bats, throws, strike_zone_top, strike_zone_bottom, primary_position_code, debut_season, created_at, updated_at)
VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 first_name = EXCLUDED.first_name,
 last_name = EXCLUDED.last_name,
 full_name = EXCLUDED.full_name,
 primary_number = EXCLUDED.primary_number,
 birth_date = EXCLUDED.birth_date,
 weight = EXCLUDED.weight,
 active = EXCLUDED.active,
 bats = EXCLUDED.bats,
 throws = EXCLUDED.throws,
 strike_zone_top = EXCLUDED.strike_zone_top,
 strike_zone_bottom = EXCLUDED.strike_zone_bottom,
 primary_position_code = EXCLUDED.primary_position_code,
 debut_season = EXCLUDED.debut_season,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM players WHERE id = ?;
")

(def ^:private get-earliest-active-season-query
  "
SELECT min(debut_season) FROM players WHERE active = true;
")

(def ^:private get-active-player-ids-query
  "
SELECT id FROM players WHERE active = true;
")

(defn- escape
  [value]
  (string/escape value {\' "''"}))

(defn upsert
  [ds player]
  (db-core/execute-one!
   ds
   [upsert-query (:id player) (escape (:first-name player)) (escape (:last-name player)) (escape (:full-name player)) (:primary-number player) (:birth-date player) (:weight player) (:active player) (:bats player) (:throws player) (:strike-zone-top player) (:strike-zone-bottom player) (:primary-position-code player) (:debut-season player)]))

(defn upsert-batch
  [ds players]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [player] [(:id player) (escape (:first-name player)) (escape (:last-name player)) (escape (:full-name player)) (:primary-number player) (:birth-date player) (:weight player) (:active player) (:bats player) (:throws player) (:strike-zone-top player) (:strike-zone-bottom player) (:primary-position-code player) (:debut-season player)]) players)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))

(defn get-earliest-active-season
  [ds]
  (:min
   (first
    (db-core/execute!
     ds
     [get-earliest-active-season-query]))))

(defn get-active-player-ids
  [ds]
  (map
   :id
   (db-core/execute!
    ds
    [get-active-player-ids-query])))
