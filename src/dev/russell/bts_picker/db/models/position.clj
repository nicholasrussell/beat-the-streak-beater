(ns dev.russell.bts-picker.db.models.position
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO positions (code, abbreviation, display_name, type, game_position, fielder, outfield, pitcher, created_at, updated_at)
VALUES('%s', '%s', '%s', '%s', %b, %b, %b, %b, now(), now())
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
SELECT * FROM positions WHERE code = '%s';
")

(defn upsert
  [ds position]
  (jdbc/execute-one! ds
                     [(format upsert-query (:code position) (:abbreviation position) (:display-name position) (:type position) (:game-position position) (:fielder position) (:outfield position) (:pitcher position))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-code
  [ds code]
  (jdbc/execute-one! ds
                     [(format get-by-code-query code)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
