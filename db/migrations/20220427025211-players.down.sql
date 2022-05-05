ALTER TABLE players
DROP CONSTRAINT IF EXISTS fk_debut_season;
--;;
ALTER TABLE players
DROP CONSTRAINT IF EXISTS fk_position;
--;;
DROP TABLE IF EXISTS players;
