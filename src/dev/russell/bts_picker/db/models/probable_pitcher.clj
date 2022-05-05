(ns dev.russell.bts-picker.db.models.probable-pitcher
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO probable_pitchers (player_id, game_id, side, created_at, updated_at)
VALUES(%d, %d, '%s', now(), now())
ON CONFLICT (game_id, player_id)
DO UPDATE SET
 side = EXCLUDED.side,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-game-id-query
"
SELECT * FROM probable_pitchers WHERE game_id = %d;
")

(defn upsert
  [ds probable-pitcher]
  (jdbc/execute-one! ds
                     [(format upsert-query (:player-id probable-pitcher) (:game-id probable-pitcher) (:side probable-pitcher))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-game-id
  [ds id]
  (jdbc/execute! ds
                 [(format get-by-game-id-query id)]
                 {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn upsert-probable-pitchers
  [ds schedule]
  (->> schedule
       :games
       (mapcat (fn [game]
                 [{:game-id (:gamePk game)
                   :player-id (-> game :teams :away :probablePitcher :id)
                   :side "away"}
                  {:game-id (:gamePk game)
                   :player-id (-> game :teams :home :probablePitcher :id)
                   :side "home"}]))
       (remove #(nil? (:player-id %)))
       (map (partial upsert ds))
       doall))
