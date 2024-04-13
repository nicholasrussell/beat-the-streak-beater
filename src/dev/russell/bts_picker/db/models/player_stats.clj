(ns dev.russell.bts-picker.db.models.player-stats
  (:require [taoensso.timbre :as log]
            [dev.russell.bts-picker.db.core :as db-core]))

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
  ?,
  ?,

  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,

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
  ?,
  ?,

  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,

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
  ?,
  ?,

  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,
  ?,

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
SELECT player_id, season FROM player_stats_batting WHERE player_id = ? AND season = ?;
")

(def ^:private player-pitching-stats-already-stored-query
  "
SELECT player_id, season FROM player_stats_pitching WHERE player_id = ? AND season = ?;
")

(def ^:private season-batting-aggregates-query
  "
SELECT
	SUM(i.plate_appearances) AS plate_appearances_total, MIN(i.plate_appearances) AS plate_appearances_min, MAX(i.plate_appearances) AS plate_appearances_max, AVG(i.plate_appearances) AS plate_appearances_avg, STDDEV_POP(i.plate_appearances) AS plate_appearances_std_dev,
	SUM(i.hits) AS hits_total, MIN(i.hits) AS hits_min, MAX(i.hits) AS hits_max, AVG(i.hits) AS hits_avg, STDDEV_POP(i.hits) AS hits_std_dev,
	MIN(i.hits_percentage) AS hits_percentage_min, MAX(i.hits_percentage) AS hits_percentage_max, AVG(i.hits_percentage) AS hits_percentage_avg, STDDEV_POP(i.hits_percentage) AS hits_percentage_std_dev
FROM
  (SELECT stats.*, stats.hits::float / stats.plate_appearances::float AS hits_percentage
   FROM player_stats_batting stats
   WHERE season = ? AND plate_appearances > 0) i;
")

(def ^:private season-pitching-aggregates-query
  "
SELECT
	SUM(i.batters_faced) AS batters_faced_total, MIN(i.batters_faced) AS batters_faced_min, MAX(i.batters_faced) AS batters_faced_max, AVG(i.batters_faced) AS batters_faced_avg, STDDEV_POP(i.batters_faced) AS batters_faced_std_dev,
	SUM(i.hits) AS hits_total, MIN(i.hits) AS hits_min, MAX(i.hits) AS hits_max, AVG(i.hits) AS hits_avg, STDDEV_POP(i.hits) AS hits_std_dev,
	MIN(i.hits_per_batter_faced) AS hits_per_batter_faced_min, MAX(i.hits_per_batter_faced) AS hits_per_batter_faced_max, AVG(i.hits_per_batter_faced) AS hits_per_batter_faced_avg, STDDEV_POP(i.hits_per_batter_faced) AS hits_per_batter_faced_std_dev
FROM
  (SELECT stats.*, stats.hits::float / stats.batters_faced::float AS hits_per_batter_faced
   FROM player_stats_pitching stats
   WHERE season = ? AND batters_faced > 0) i
")

(def ^:private player-season-batting-stats-query
  "
SELECT psb.*, CASE WHEN psb.plate_appearances = 0 THEN 0 ELSE (psb.hits::float / psb.plate_appearances::float) END AS hits_percentage
FROM player_stats_batting psb
WHERE psb.player_id = ANY(?) AND psb.season = ?;
")

(def ^:private player-season-pitching-stats-query
  "
SELECT psp.*, CASE WHEN psp.batters_faced = 0 THEN 0 ELSE (psp.hits::float / psp.batters_faced::float) END AS hits_per_batter_faced
FROM player_stats_pitching psp
WHERE psp.player_id = ANY(?) AND psp.season = ?;
")

(def ^:private batter-vs-pitcher-stats-query
  "
SELECT bvp.*, CASE WHEN bvp.plate_appearances = 0 THEN 0 ELSE (bvp.hits::float / bvp.plate_appearances::float) END AS hits_percentage
FROM batter_vs_pitcher bvp
WHERE bvp.batter_id = ? AND bvp.pitcher_id = ?;
")

(defn upsert-batting-stats
  [ds pbs]
  (db-core/execute-one!
   ds
   [upsert-batting-stats-query
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
    (:triples pbs)]))

(defn upsert-batting-stats-batch
  [ds pbss]
  (db-core/execute-batch!
   ds
   upsert-batting-stats-query
   (mapv
    (fn [pbs]
      [(:player-id pbs)
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
       (:triples pbs)])
    pbss)))

(defn upsert-pitching-stats
  [ds pps]
  (db-core/execute-one!
   ds
   [upsert-pitching-stats-query
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
    (:wins pps)]))

(defn upsert-pitching-stats-batch
  [ds ppss]
  (db-core/execute-batch!
   ds
   upsert-pitching-stats-query
   (mapv
    (fn [pps]
      [(:player-id pps)
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
       (:wins pps)])
    ppss)))

(defn upsert-batter-vs-pitcher-stats
  [ds bvp]
  (db-core/execute-one!
   ds
   [upsert-batter-vs-pitcher-stats-query
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
    (:triples bvp)]))

(defn upsert-batter-vs-pitcher-stats-batch
  [ds bvps]
  (db-core/execute-batch!
   ds
   upsert-batter-vs-pitcher-stats-query
   (mapv
    (fn [bvp]
      [(:batter-id bvp)
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
       (:triples bvp)])
    bvps)))

(defn player-batting-stats-already-stored
  [ds player-id season]
  (some?
   (db-core/execute-one!
    ds
    [player-batting-stats-already-stored-query player-id season])))

(defn player-pitching-stats-already-stored
  [ds player-id season]
  (some?
   (db-core/execute-one!
    ds
    [player-pitching-stats-already-stored-query player-id season])))

(defn season-batting-aggregates
  [ds season]
  (db-core/execute-one!
   ds
   [season-batting-aggregates-query season]))

(defn season-pitching-aggregates
  [ds season]
  (db-core/execute-one!
   ds
   [season-pitching-aggregates-query season]))

(defn player-season-batting-stats
  [ds season player-ids]
  (db-core/execute!
   ds
   [player-season-batting-stats-query (int-array player-ids) season]))

(defn player-season-pitching-stats
  [ds season player-ids]
  (db-core/execute!
   ds
   [player-season-pitching-stats-query (int-array player-ids) season]))

(defn batter-vs-pitcher-stats
  [ds batter-id pitcher-id]
  (db-core/execute-one!
   ds
   [batter-vs-pitcher-stats-query batter-id pitcher-id]))

