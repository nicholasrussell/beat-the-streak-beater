CREATE TABLE IF NOT EXISTS skies (
  code varchar(25) PRIMARY KEY,
  description varchar(25),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
