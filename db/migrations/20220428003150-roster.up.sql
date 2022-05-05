CREATE TABLE IF NOT EXISTS rosters (
  player_id integer PRIMARY KEY,
  team_id integer,
  status character(1),
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_player
    FOREIGN KEY(player_id)
	  REFERENCES players(id),
  CONSTRAINT fk_team
    FOREIGN KEY(team_id)
	  REFERENCES teams(id)
);
