(ns dev.russell.bts-picker.db.models.player-stats
  (:require [taoensso.timbre :as log]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-batting-stats-query
 "
INSERT INTO player_stats_batting (
  player_id,
  season,

  air_outs,
  at_bats,
  base_on_balls,
  doubles,
  games_played,
  ground_into_double_play,
  ground_outs,
  hit_by_pitch,
  hits,
  home_runs,
  intentional_walks,
  left_on_base,
  number_of_pitches,
  plate_appearances,
  rbi,
  runs,
  sac_bunts,
  sac_flies,
  strike_outs,
  stolen_bases,
  total_bases,
  triples,

  created_at,
  updated_at
)
VALUES (
  %d,
  '%s',

  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,

  now(),
  now()
)
ON CONFLICT (player_id, season)
DO UPDATE SET
  air_outs = EXCLUDED.air_outs,
  at_bats = EXCLUDED.at_bats,
  base_on_balls = EXCLUDED.base_on_balls,
  doubles = EXCLUDED.doubles,
  games_played = EXCLUDED.games_played,
  ground_into_double_play = EXCLUDED.ground_into_double_play,
  ground_outs = EXCLUDED.ground_outs,
  hit_by_pitch = EXCLUDED.hit_by_pitch,
  hits = EXCLUDED.hits,
  home_runs = EXCLUDED.home_runs,
  intentional_walks = EXCLUDED.intentional_walks,
  left_on_base = EXCLUDED.left_on_base,
  number_of_pitches = EXCLUDED.number_of_pitches,
  plate_appearances = EXCLUDED.plate_appearances,
  rbi = EXCLUDED.rbi,
  runs = EXCLUDED.runs,
  sac_bunts = EXCLUDED.sac_bunts,
  sac_flies = EXCLUDED.sac_flies,
  strike_outs = EXCLUDED.strike_outs,
  stolen_bases = EXCLUDED.stolen_bases,
  total_bases = EXCLUDED.total_bases,
  triples = EXCLUDED.triples,

  updated_at = EXCLUDED.updated_at;
")

(def ^:private upsert-pitching-stats-query
  "
INSERT INTO player_stats_pitching (
  player_id,
  season,

  air_outs,
  at_bats,
  balks,
  base_on_balls,
  batters_faced,
  blown_saves,
  catchers_interference,
  caught_stealing,
  complete_games,
  doubles,
  earned_runs,
  games_finished,
  games_pitched,
  games_played,
  games_started,
  ground_into_double_play,
  ground_outs,
  hit_batsmen,
  hit_by_pitch,
  hits,
  holds,
  home_runs,
  inherited_runners,
  inherited_runners_scored,
  innings_pitched,
  innings_pitched_complete,
  innings_pitched_partial,
  intentional_walks,
  losses,
  number_of_pitches,
  outs,
  pickoffs,
  runs,
  sac_bunts,
  sac_flies,
  save_opportunities,
  saves,
  shutouts,
  stolen_bases,
  strike_outs,
  strikes,
  total_bases,
  triples,
  wild_pitches,
  wins,

  created_at,
  updated_at
)
VALUES (
  %d,
  '%s',

  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  '%s',
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,

  now(),
  now()
)
ON CONFLICT (player_id, season)
DO UPDATE SET
  air_outs = EXCLUDED.air_outs,
  at_bats = EXCLUDED.at_bats,
  balks = EXCLUDED.balks,
  base_on_balls = EXCLUDED.base_on_balls,
  batters_faced = EXCLUDED.batters_faced,
  blown_saves = EXCLUDED.blown_saves,
  catchers_interference = EXCLUDED.catchers_interference,
  caught_stealing = EXCLUDED.caught_stealing,
  complete_games = EXCLUDED.complete_games,
  doubles = EXCLUDED.doubles,
  earned_runs = EXCLUDED.earned_runs,
  games_finished = EXCLUDED.games_finished,
  games_pitched = EXCLUDED.games_pitched,
  games_played = EXCLUDED.games_played,
  games_started = EXCLUDED.games_started,
  ground_into_double_play = EXCLUDED.ground_into_double_play,
  ground_outs = EXCLUDED.ground_outs,
  hit_batsmen = EXCLUDED.hit_batsmen,
  hit_by_pitch = EXCLUDED.hit_by_pitch,
  hits = EXCLUDED.hits,
  holds = EXCLUDED.holds,
  home_runs = EXCLUDED.home_runs,
  inherited_runners = EXCLUDED.inherited_runners,
  inherited_runners_scored = EXCLUDED.inherited_runners_scored,
  innings_pitched = EXCLUDED.innings_pitched,
  innings_pitched_complete = EXCLUDED.innings_pitched_complete,
  innings_pitched_partial = EXCLUDED.innings_pitched_partial,
  intentional_walks = EXCLUDED.intentional_walks,
  losses = EXCLUDED.losses,
  number_of_pitches = EXCLUDED.number_of_pitches,
  outs = EXCLUDED.outs,
  pickoffs = EXCLUDED.pickoffs,
  runs = EXCLUDED.runs,
  sac_bunts = EXCLUDED.sac_bunts,
  sac_flies = EXCLUDED.sac_flies,
  save_opportunities = EXCLUDED.save_opportunities,
  saves = EXCLUDED.saves,
  shutouts = EXCLUDED.shutouts,
  stolen_bases = EXCLUDED.stolen_bases,
  strike_outs = EXCLUDED.strike_outs,
  strikes = EXCLUDED.strikes,
  total_bases = EXCLUDED.total_bases,
  triples = EXCLUDED.triples,
  wild_pitches = EXCLUDED.wild_pitches,
  wins = EXCLUDED.wins,

  updated_at = EXCLUDED.updated_at;
")

(def ^:private upsert-batter-vs-pitcher-stats-query
  "
INSERT INTO batter_vs_pitcher (
  batter_id,
  pitcher_id,

  air_outs,
  at_bats,
  base_on_balls,
  catchers_interference,
  doubles,
  games_played,
  ground_into_double_play,
  ground_into_triple_play,
  ground_outs,
  hit_by_pitch,
  hits,
  home_runs,
  intentional_walks,
  left_on_base,
  number_of_pitches,
  plate_appearances,
  rbi,
  sac_bunts,
  sac_flies,
  strike_outs,
  total_bases,
  triples,

  created_at,
  updated_at
)
VALUES (
  %d,
  %d,

  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,
  %d,

  now(),
  now()
)
ON CONFLICT (batter_id, pitcher_id)
DO UPDATE SET
  air_outs = EXCLUDED.air_outs,
  at_bats = EXCLUDED.at_bats,
  base_on_balls = EXCLUDED.base_on_balls,
  catchers_interference = EXCLUDED.catchers_interference,
  doubles = EXCLUDED.doubles,
  games_played = EXCLUDED.games_played,
  ground_into_double_play = EXCLUDED.ground_into_double_play,
  ground_into_triple_play = EXCLUDED.ground_into_triple_play,
  ground_outs = EXCLUDED.ground_outs,
  hit_by_pitch = EXCLUDED.hit_by_pitch,
  hits = EXCLUDED.hits,
  home_runs = EXCLUDED.home_runs,
  intentional_walks = EXCLUDED.intentional_walks,
  left_on_base = EXCLUDED.left_on_base,
  number_of_pitches = EXCLUDED.number_of_pitches,
  plate_appearances = EXCLUDED.plate_appearances,
  rbi = EXCLUDED.rbi,
  sac_bunts = EXCLUDED.sac_bunts,
  sac_flies = EXCLUDED.sac_flies,
  strike_outs = EXCLUDED.strike_outs,
  total_bases = EXCLUDED.total_bases,
  triples = EXCLUDED.triples,

  updated_at = EXCLUDED.updated_at;
")

(def ^:private player-batting-stats-already-stored-query
 "
SELECT player_id, season FROM player_stats_batting WHERE player_id = %d AND season = '%s';
")

(def ^:private player-pitching-stats-already-stored-query
 "
SELECT player_id, season FROM player_stats_pitching WHERE player_id = %d AND season = '%s';
")

(def ^:private season-hits-aggregates-query
  "
SELECT COUNT(*), SUM(hits), MIN(hits), MAX(hits), AVG(hits), STDDEV_POP(hits) FROM player_stats_batting WHERE season = '%s';
")

(defn upsert-batting-stats
  [ds pbs]
  (jdbc/execute-one! ds
                     [(format upsert-batting-stats-query
                              (:player-id pbs)
                              (:season pbs)
                              (:air-outs pbs)
                              (:at-bats pbs)
                              (:base-on-balls pbs)
                              (:doubles pbs)
                              (:games-played pbs)
                              (:ground-into-double-play pbs)
                              (:ground-outs pbs)
                              (:hit-by-pitch pbs)
                              (:hits pbs)
                              (:home-runs pbs)
                              (:intentional-walks pbs)
                              (:left-on-base pbs)
                              (:number-of-pitches pbs)
                              (:plate-appearances pbs)
                              (:rbi pbs)
                              (:runs pbs)
                              (:sac-bunts pbs)
                              (:sac-flies pbs)
                              (:stolen-bases pbs)
                              (:strike-outs pbs)
                              (:total-bases pbs)
                              (:triples pbs))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn upsert-pitching-stats
  [ds pps]
  (jdbc/execute-one! ds
                     [(format upsert-pitching-stats-query
                              (:player-id pps)
                              (:season pps)
                              (:air-outs pps)
                              (:at-bats pps)
                              (:balks pps)
                              (:base-on-balls pps)
                              (:batters-faced pps)
                              (:blown-saves pps)
                              (:catchers-interference pps)
                              (:caught-stealing pps)
                              (:complete-games pps)
                              (:doubles pps)
                              (:earned-runs pps)
                              (:games-finished pps)
                              (:games-pitched pps)
                              (:games-played pps)
                              (:games-started pps)
                              (:ground-into-double-play pps)
                              (:ground-outs pps)
                              (:hit-batsmen pps)
                              (:hit-by-pitch pps)
                              (:hits pps)
                              (:holds pps)
                              (:home-runs pps)
                              (:inherited-runners pps)
                              (:inherited-runners-scored pps)
                              (:innings-pitched pps)
                              (:innings-pitched-complete pps)
                              (:innings-pitched-partial pps)
                              (:intentional-walks pps)
                              (:losses pps)
                              (:number-of-pitches pps)
                              (:outs pps)
                              (:pickoffs pps)
                              (:runs pps)
                              (:sac-bunts pps)
                              (:sac-flies pps)
                              (:save-opportunities pps)
                              (:saves pps)
                              (:shutouts pps)
                              (:stolen-bases pps)
                              (:strike-outs pps)
                              (:strikes pps)
                              (:total-bases pps)
                              (:triples pps)
                              (:wild-pitches pps)
                              (:wins pps))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn upsert-batter-vs-pitcher-stats
  [ds bvp]
  (jdbc/execute-one! ds
                     [(format upsert-batter-vs-pitcher-stats-query
                              (:batter-id bvp)
                              (:pitcher-id bvp)
                              (:air-outs bvp)
                              (:at-bats bvp)
                              (:base-on-balls bvp)
                              (:catchers-interference bvp)
                              (:doubles bvp)
                              (:games-played bvp)
                              (:ground-into-double-play bvp)
                              (:ground-into-triple-play bvp)
                              (:ground-outs bvp)
                              (:hit-by-pitch bvp)
                              (:hits bvp)
                              (:home-runs bvp)
                              (:intentional-walks bvp)
                              (:left-on-base bvp)
                              (:number-of-pitches bvp)
                              (:plate-appearances bvp)
                              (:rbi bvp)
                              (:sac-bunts bvp)
                              (:sac-flies bvp)
                              (:strike-outs bvp)
                              (:total-bases bvp)
                              (:triples bvp))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn player-batting-stats-already-stored
  [ds player-id season]
  (some?
   (jdbc/execute-one! ds
                      [(format player-batting-stats-already-stored-query player-id season)]
                      {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))

(defn player-pitching-stats-already-stored
  [ds player-id season]
  (some?
   (jdbc/execute-one! ds
                      [(format player-pitching-stats-already-stored-query player-id season)]
                      {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))

(defn season-hits-aggregates
  [ds season]
  (jdbc/execute-one! ds
                     [(format season-hits-aggregates-query season)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))