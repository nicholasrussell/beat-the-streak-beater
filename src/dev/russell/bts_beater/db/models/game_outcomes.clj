(ns dev.russell.bts-beater.db.models.game-outcomes
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO game_outcomes (game_id, player_id, batting_order, hits, plate_appearances, created_at)
VALUES (?, ?, ?, ?, ?, now())
ON CONFLICT (game_id, player_id)
DO NOTHING
RETURNING *;
")

(defn upsert-batch
  [ds game-outcomes]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [outcome] [(:game-id outcome) (:player-id outcome) (:batting-order outcome) (:hits outcome) (:plate-appearances outcome)]) game-outcomes)))

