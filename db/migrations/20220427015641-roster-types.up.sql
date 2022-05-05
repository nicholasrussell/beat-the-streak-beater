CREATE TABLE IF NOT EXISTS roster_types (
  parameter varchar(25) PRIMARY KEY,
  description varchar(75),
  lookup_name varchar(25),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
