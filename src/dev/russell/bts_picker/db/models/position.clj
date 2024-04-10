(ns dev.russell.bts-picker.db.models.position
  (:require [dev.russell.bts-picker.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO positions (code, abbreviation, display_name, type, game_position, fielder, outfield, pitcher, created_at, updated_at)
VALUES(?, ?, ?, ?, ?, ?, ?, ?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 abbreviation = EXCLUDED.abbreviation,
 display_name = EXCLUDED.display_name,
 type = EXCLUDED.type,
 game_position = EXCLUDED.game_position,
 fielder = EXCLUDED.fielder,
 outfield = EXCLUDED.outfield,
 pitcher = EXCLUDED.pitcher,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM positions WHERE code = ?;
")

(defn upsert
  [ds position]
  (db-core/execute-one!
   ds
   [upsert-query (:code position) (:abbreviation position) (:display-name position) (:type position) (:game-position position) (:fielder position) (:outfield position) (:pitcher position)]))

(defn upsert-batch
  [ds positions]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [position] [(:code position) (:abbreviation position) (:display-name position) (:type position) (:game-position position) (:fielder position) (:outfield position) (:pitcher position)]) positions)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
