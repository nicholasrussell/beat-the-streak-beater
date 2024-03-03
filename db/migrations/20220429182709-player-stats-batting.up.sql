CREATE TABLE IF NOT EXISTS player_stats_batting (
  player_id integer NOT NULL,
  season character(4) NOT NULL,

  air_outs integer,
  at_bats integer,
  base_on_balls integer,
  doubles integer,
  games_played integer,
  ground_into_double_play integer,
  ground_outs integer,
  hit_by_pitch integer,
  hits integer,
  home_runs integer,
  intentional_walks integer,
  left_on_base integer,
  number_of_pitches integer,
  plate_appearances integer,
  rbi integer,
  runs integer,
  sac_bunts integer,
  sac_flies integer,
  stolen_bases integer,
  strike_outs integer,
  total_bases integer,
  triples integer,
  
  created_at timestamp with time zone,
  updated_at timestamp with time zone,

  PRIMARY KEY(player_id, season),
  CONSTRAINT fk_player
    FOREIGN KEY(player_id)
    REFERENCES players(id),
  CONSTRAINT fk_season
    FOREIGN KEY(season)
    REFERENCES seasons(id)
);
