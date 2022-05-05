CREATE TABLE IF NOT EXISTS games (
  id integer PRIMARY KEY,
  date character(10),
  date_time timestamp with time zone,
  away_team integer,
  home_team integer,
  series_game_number integer,
  games_in_series integer,
  season character(4),
  game_type character(1),
  venue integer,
  double_header boolean,
  day_night varchar(5),
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_away_team
    FOREIGN KEY(away_team)
    REFERENCES teams(id),
  CONSTRAINT fk_home_team
    FOREIGN KEY(home_team)
    REFERENCES teams(id),
  CONSTRAINT fk_season
    FOREIGN KEY(season)
	  REFERENCES seasons(id),
  CONSTRAINT fk_game_type
    FOREIGN KEY(game_type)
    REFERENCES game_types(id),
  CONSTRAINT fk_venue
    FOREIGN KEY(venue)
    REFERENCES venues(id)
);
