CREATE TABLE IF NOT EXISTS player_stats_batting (
  player_id integer NOT NULL,
  season character(4) NOT NULL,
  games_played integer,
  hits integer,
  at_bats integer,
  plate_appearances integer,
  ground_outs integer,
  air_outs integer,
  strike_outs integer,
  base_on_balls integer,
  runs integer,
  doubles integer,
  triples integer,
  home_runs integer,
  total_bases integer,
  rbi integer,
  left_on_base integer,
  intentional_walks integer,
  hit_by_pitch integer,
  ground_into_double_play integer,
  number_of_pitches integer,
  sac_bunts integer,
  sac_flies integer,
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_player
    FOREIGN KEY(player_id)
    REFERENCES players(id),
  CONSTRAINT fk_season
    FOREIGN KEY(season)
    REFERENCES seasons(id),
  CONSTRAINT uniq_player_season
    UNIQUE (player_id, season)
);
