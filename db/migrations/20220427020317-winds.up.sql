CREATE TABLE IF NOT EXISTS winds (
  code varchar(15) PRIMARY KEY,
  description varchar(15),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
