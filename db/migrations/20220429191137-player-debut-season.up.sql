ALTER TABLE players
ADD COLUMN debut_season character(4),
ADD CONSTRAINT fk_debut_season
  FOREIGN KEY(debut_season)
  REFERENCES seasons(id);