(ns dev.russell.bts-picker.db.models.player-stats
  (:require [taoensso.timbre :as log]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-batting-stats-query
"
INSERT INTO player_stats_batting (player_id, season, games_played, hits, at_bats, plate_appearances, ground_outs, air_outs, strike_outs, base_on_balls, runs, doubles, triples, home_runs, total_bases, rbi, left_on_base, intentional_walks, hit_by_pitch, ground_into_double_play, number_of_pitches, sac_bunts, sac_flies, created_at, updated_at)
VALUES(%d, '%s', %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, now(), now())
ON CONFLICT (player_id, season)
DO UPDATE SET
 games_played = EXCLUDED.games_played,
 hits = EXCLUDED.hits,
 at_bats = EXCLUDED.at_bats,
 plate_appearances = EXCLUDED.plate_appearances,
 ground_outs = EXCLUDED.ground_outs,
 air_outs = EXCLUDED.air_outs,
 strike_outs = EXCLUDED.strike_outs,
 base_on_balls = EXCLUDED.base_on_balls,
 runs = EXCLUDED.runs,
 doubles = EXCLUDED.doubles,
 triples = EXCLUDED.triples,
 home_runs = EXCLUDED.home_runs,
 total_bases = EXCLUDED.total_bases,
 rbi = EXCLUDED.rbi,
 left_on_base = EXCLUDED.left_on_base,
 intentional_walks = EXCLUDED.intentional_walks,
 hit_by_pitch = EXCLUDED.hit_by_pitch,
 ground_into_double_play = EXCLUDED.ground_into_double_play,
 number_of_pitches = EXCLUDED.number_of_pitches,
 sac_bunts = EXCLUDED.sac_bunts,
 sac_flies = EXCLUDED.sac_flies,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private player-batting-stats-already-stored-query
"
SELECT player_id, season FROM player_stats_batting WHERE player_id = %d AND season = '%s';
")

(defn upsert-batting-stats
  [ds pbs]
  (jdbc/execute-one! ds
                     [(format upsert-batting-stats-query (:player-id pbs) (:season pbs) (:games-played pbs) (:hits pbs) (:at-bats pbs) (:plate-appearances pbs) (:ground-outs pbs) (:air-outs pbs) (:strike-outs pbs) (:base-on-balls pbs) (:runs pbs) (:doubles pbs) (:triples pbs) (:home-runs pbs) (:total-bases pbs) (:rbi pbs) (:left-on-base pbs) (:intentional-walks pbs) (:hit-by-pitch pbs) (:ground-into-double-play pbs) (:number-of-pitches pbs) (:sac-bunts pbs) (:sac-flies pbs))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn player-batting-stats-already-stored
  [ds player-id season]
  (some?
   (jdbc/execute-one! ds
                      [(format player-batting-stats-already-stored-query player-id season)]
                      {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))
