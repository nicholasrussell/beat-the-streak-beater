CREATE TABLE IF NOT EXISTS probable_pitchers (
  player_id integer,
  game_id integer,
  side character(4),
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_player
    FOREIGN KEY(player_id)
    REFERENCES players(id),
  CONSTRAINT fk_game
    FOREIGN KEY(game_id)
    REFERENCES games(id),
  CONSTRAINT game_player_uniq
    UNIQUE (game_id, player_id)
);
--;;
CREATE INDEX IF NOT EXISTS idx_player ON probable_pitchers (
  player_id
);
--;;
CREATE INDEX IF NOT EXISTS idx_game ON probable_pitchers (
  game_id
);
