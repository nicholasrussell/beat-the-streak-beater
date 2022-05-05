CREATE TABLE IF NOT EXISTS hit_trajectories (
  code varchar(25) PRIMARY KEY,
  description varchar(50),
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
