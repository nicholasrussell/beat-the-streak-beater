CREATE TABLE IF NOT EXISTS standing_types (
  name varchar(25) PRIMARY KEY,
  description varchar(256),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
