(ns dev.russell.bts-beater.games
  (:require [dev.russell.bts-beater.util :as util]))

(def ^:private base-url "http://gd2.mlb.com/components/game/mlb")

(def ^:private url-paths {:boxscore "boxscore.json",
                          :game-events "game_events.json",
                          :linescore "linescore.json",
                          :plays "plays.json",
                          :scoreboard "master_scoreboard.json"})

(defn- scoreboard-url
  [date]
  (str base-url
       (format "/year_%s/month_%s/day_%s/" (util/date->year date) (util/date->month date) (util/date->day date))
       (:scoreboard url-paths)))

(defn- scoreboard-data
  [date]
  (util/get-json (scoreboard-url date)))

(defn- make-team-name
  [game team]
  (str ((keyword (str team "_team_city")) game) " " ((keyword (str team "_team_name")) game)))

(defn- transform-game
  [game]
  {:id (:id game)
   :away-team-id (:away_team_id game)
   :home-team-id (:home_team_id game)
   :away-team-name (make-team-name game "away")
   :home-team-name (make-team-name game "home")
   :away-team-abbr (:away_name_abbrev game)
   :home-team-abbr (:home_name_abbrev game)
   :venue-id (:venue_id game)
   :venue-name (:venue game)
   :venue-location (:location game)})

(defn- get-games
  [date]
  (let [scoreboard (scoreboard-data date)
        games (:game (:games (:data scoreboard)))]
    (mapv transform-game games)))

(defn games
  ([] (games (util/now)))
  ([date] (get-games date)))
