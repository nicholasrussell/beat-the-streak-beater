CREATE TABLE IF NOT EXISTS players (
  id integer PRIMARY KEY,
  first_name varchar(128),
  last_name varchar(128),
  full_name varchar(255),
  primary_number varchar(2),
  birth_date varchar(10),
  height varchar(7),
  weight integer,
  active boolean,
  bats character(1),
  throws character(1),
  strike_zone_top decimal(4, 2),
  strike_zone_bottom decimal(4, 2),
  primary_position_code varchar(2),
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  CONSTRAINT fk_position
    FOREIGN KEY(primary_position_code)
	  REFERENCES positions(code)
);
