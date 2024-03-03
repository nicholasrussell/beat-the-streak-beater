ALTER TABLE IF EXISTS batter_vs_pitcher
DROP CONSTRAINT IF EXISTS pitcher_id;
--;;
ALTER TABLE IF EXISTS batter_vs_pitcher
DROP CONSTRAINT IF EXISTS batter_id;
--;;
DROP TABLE IF EXISTS batter_vs_pitcher;
