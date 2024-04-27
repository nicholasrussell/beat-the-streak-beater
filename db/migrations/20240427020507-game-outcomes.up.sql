CREATE TABLE IF NOT EXISTS game_outcomes (
    game_id integer NOT NULL,
    player_id integer NOT NULL,
    batting_order varchar(3),
    hits integer,
    plate_appearances integer,

    created_at timestamp with time zone NOT NULL DEFAULT now(),

    PRIMARY KEY(game_id, player_id),
    CONSTRAINT fk_game
      FOREIGN KEY(game_id)
      REFERENCES games(id),
    CONSTRAINT fk_player
      FOREIGN KEY(player_id)
      REFERENCES players(id)
);
