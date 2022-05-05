CREATE TABLE IF NOT EXISTS metrics (
  id integer PRIMARY KEY,
  name varchar(50),
  unit varchar(6),
  stat_group_codes varchar(10)[],
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
