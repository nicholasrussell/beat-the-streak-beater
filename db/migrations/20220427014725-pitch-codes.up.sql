CREATE TABLE IF NOT EXISTS pitch_codes (
  code varchar(3) PRIMARY KEY,
  description varchar(50),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
