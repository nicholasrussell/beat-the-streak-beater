CREATE TABLE IF NOT EXISTS positions (
  code varchar(2) PRIMARY KEY,
  abbreviation varchar(4),
  display_name varchar(25),
  type varchar(25),
  game_position boolean,
  fielder boolean,
  outfield boolean,
  pitcher boolean,
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
