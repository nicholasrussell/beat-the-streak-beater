CREATE TABLE IF NOT EXISTS seasons (
  id character(4) PRIMARY KEY,
  spring_start_date character(10),
  spring_end_date character(10),
  regular_season_start_date character(10),
  regular_season_end_date character(10),
  post_season_start_date character(10),
  post_season_end_date character(10),
  current boolean,
  created_at timestamp with time zone,
  updated_at timestamp with time zone
);
