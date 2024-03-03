CREATE TABLE IF NOT EXISTS batter_vs_pitcher (
  batter_id integer NOT NULL,
  pitcher_id integer NOT NULL,

  air_outs integer,
  at_bats integer,
  base_on_balls integer,
  catchers_interference integer,
  doubles integer,
  games_played integer,
  ground_into_double_play integer,
  ground_into_triple_play integer,
  ground_outs integer,
  hit_by_pitch integer,
  hits integer,
  home_runs integer,
  intentional_walks integer,
  left_on_base integer,
  number_of_pitches integer,
  plate_appearances integer,
  rbi integer,
  sac_bunts integer,
  sac_flies integer,
  strike_outs integer,
  total_bases integer,
  triples integer,

  created_at timestamp with time zone,
  updated_at timestamp with time zone,

  PRIMARY KEY(batter_id, pitcher_id),
  CONSTRAINT fk_batter_id
    FOREIGN KEY(batter_id)
    REFERENCES players(id),
  CONSTRAINT fk_pitcher_id
    FOREIGN KEY(pitcher_id)
    REFERENCES players(id)
);
