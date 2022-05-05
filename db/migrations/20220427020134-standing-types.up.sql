CREATE TABLE IF NOT EXISTS standing_types (
  name varchar(25) PRIMARY KEY,
  description varchar(75),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
