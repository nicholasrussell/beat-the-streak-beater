ALTER TABLE players
DROP CONSTRAINT IF EXISTS fk_debut_season;
--;;
ALTER TABLE players
DROP COLUMN IF EXISTS debut_season;