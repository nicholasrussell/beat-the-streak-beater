ALTER TABLE games
DROP CONSTRAINT IF EXISTS fk_venue;
--;;
ALTER TABLE games
DROP CONSTRAINT IF EXISTS fk_game_type;
--;;
ALTER TABLE games
DROP CONSTRAINT IF EXISTS fk_season;
--;;
ALTER TABLE games
DROP CONSTRAINT IF EXISTS fk_home_team;
--;;
ALTER TABLE games
DROP CONSTRAINT IF EXISTS fk_away_team;
--;;
DROP TABLE IF EXISTS games;
