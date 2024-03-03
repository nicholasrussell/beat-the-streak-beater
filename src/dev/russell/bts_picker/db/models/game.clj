(ns dev.russell.bts-picker.db.models.game
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
 "
INSERT INTO games (id, date, date_time, away_team, home_team, series_game_number, games_in_series, season, game_type, venue, double_header, day_night, created_at, updated_at)
VALUES(%d, '%s', '%s', %d, %d, %d, %d, '%s', '%s', %d, %b, '%s', now(), now())
ON CONFLICT (id)
DO UPDATE SET
 date = EXCLUDED.date,
 date_time = EXCLUDED.date_time,
 away_team = EXCLUDED.away_team,
 home_team = EXCLUDED.home_team,
 series_game_number = EXCLUDED.series_game_number,
 games_in_series = EXCLUDED.games_in_series,
 season = EXCLUDED.season,
 game_type = EXCLUDED.game_type,
 venue = EXCLUDED.venue,
 double_header = EXCLUDED.double_header,
 day_night = EXCLUDED.day_night,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
 "
SELECT * FROM games WHERE id = %d;
")

(def ^:private get-by-date-query
 "
SELECT * FROM games WHERE date = '%s';
")

(def ^:private get-matchups-by-date-query
  "
SELECT gtr.game_id, gtr.team_id, gtr.roster, gtr.side, pp.player_id AS probable_pitcher FROM
(SELECT g.id AS game_id, r.team_id, CASE WHEN g.away_team = r.team_id THEN 'away' ELSE 'home' END AS side, array_agg(r.player_id) AS roster
 FROM games g
 LEFT JOIN rosters r
 ON g.away_team = r.team_id
 OR g.home_team = r.team_id
 WHERE g.date = '%s'
 GROUP BY g.id, r.team_id) AS gtr
LEFT JOIN probable_pitchers pp
ON gtr.game_id = pp.game_id
WHERE pp.side = gtr.side;
")

(defn upsert
  [ds game]
  (jdbc/execute-one! ds
                     [(format upsert-query (:id game) (:date game) (:date-time game) (:away-team game) (:home-team game) (:series-game-number game) (:games-in-series game) (:season game) (:game-type game) (:venue game) (:double-header game) (:day-night game))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-date
  [ds date]
  (jdbc/execute! ds
                 [(format get-by-date-query date)]
                 {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn upsert-games
  [ds schedule]
  (->> schedule
       :games
       (map (fn [game]
              {:id (:gamePk game)
               :date (:officialDate game)
               :date-time (:gameDate game)
               :away-team (:id (:team (:away (:teams game))))
               :home-team (:id (:team (:home (:teams game))))
               :series-game-number (:seriesGameNumber game)
               :games-in-series (:gamesInSeries game)
               :season (:season game)
               :game-type (:gameType game)
               :venue (:id (:venue game))
               :double-header (= (:doubleHeader game) "Y")
               :day-night (:dayNight game)}))
       (map (partial upsert ds))
       doall))

(defn get-matchups-by-date
  [ds date]
  (reduce
   (fn [acc cur]
     (let [side (keyword (:side cur))
           side-map {:team-id (:team-id cur)
                     :roster-ids (into [] (java.util.Arrays/asList (.getArray (:roster cur))))
                     :probable-pitcher-id (:probable-pitcher cur)}]
       (if-let [existing-side (get acc (:game-id cur))]
         (assoc acc (:game-id cur) (assoc existing-side side side-map))
         (assoc acc (:game-id cur) {side side-map}))))
   {}
   (jdbc/execute! ds
                  [(format get-matchups-by-date-query date)]
                  {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))

