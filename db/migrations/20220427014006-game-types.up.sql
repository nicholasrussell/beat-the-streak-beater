CREATE TABLE IF NOT EXISTS game_types (
  id character(1) PRIMARY KEY,
  description varchar(50),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
