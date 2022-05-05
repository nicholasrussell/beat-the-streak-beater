CREATE TABLE IF NOT EXISTS sports (
  id integer PRIMARY KEY,
  code varchar(10),
  name varchar(50),
  abbreviation varchar(10),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
