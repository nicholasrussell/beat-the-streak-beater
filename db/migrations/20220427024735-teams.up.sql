CREATE TABLE IF NOT EXISTS teams (
  id integer PRIMARY KEY,
  name varchar(255),
  venue_id integer,
  team_code varchar(3),
  abbreviation varchar(3),
  team_name varchar(255),
  location_name varchar(255),
  league_id integer,
  division_id integer,
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_venue
    FOREIGN KEY(venue_id) 
	  REFERENCES venues(id),
  CONSTRAINT fk_league
    FOREIGN KEY(league_id) 
	  REFERENCES leagues(id),
  CONSTRAINT fk_division
    FOREIGN KEY(division_id) 
	  REFERENCES divisions(id)
);
