(ns dev.russell.bts-picker.db.models.probable-pitcher
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO probable_pitchers (player_id, game_id, side, created_at, updated_at)
VALUES(?, ?, ?, now(), now())
ON CONFLICT (game_id, player_id)
DO UPDATE SET
 side = EXCLUDED.side,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-game-id-query
  "
SELECT * FROM probable_pitchers WHERE game_id = ?;
")

(def ^:private get-by-game-ids-query
  "
SELECT * FROM probable_pitchers WHERE game_id = ANY(?);
")

(defn upsert
  [ds probable-pitcher]
  (db-core/execute-one!
   ds
   [upsert-query (:player-id probable-pitcher) (:game-id probable-pitcher) (:side probable-pitcher)]))

(defn upsert-batch
  [ds probable-pitchers]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [pp] [(:player-id pp) (:game-id pp) (:side pp)]) probable-pitchers)))

(defn get-by-game-id
  [ds id]
  (db-core/execute!
   ds
   [get-by-game-id-query id]))

(defn get-by-game-ids
  [ds ids]
  (db-core/execute!
   ds
   [get-by-game-ids-query (into-array Integer/TYPE ids)]))
