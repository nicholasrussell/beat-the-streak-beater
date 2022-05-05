CREATE TABLE IF NOT EXISTS divisions (
  id integer PRIMARY KEY,
  code varchar(10),
  name varchar(50),
  sport_id integer,
  league_id integer,
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_sport
    FOREIGN KEY(sport_id) 
	  REFERENCES sports(id),
  CONSTRAINT fk_league
    FOREIGN KEY(league_id) 
	  REFERENCES leagues(id)
);
