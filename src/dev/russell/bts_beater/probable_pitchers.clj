(ns dev.russell.bts-beater.probable-pitchers
  (:require [dev.russell.batboy.schedules.core :as schedule]
            [dev.russell.bts-beater.constants :refer [SPORT_ID_MLB LEAGUE_ID_AL LEAGUE_ID_NL]]
            [dev.russell.bts-beater.util :as util]))

(defn- probable-pitcher-data-from-game
  [game side]
  (let [id (-> game :teams side :probablePitcher :id)
        team-id (-> game :teams side :team :id)]
    {:id id
     :team-id team-id}))

(defn probable-pitchers
  ([] (probable-pitchers (util/now)))
  ([date]
   (let [schedules (schedule/get-schedules
                    {:multi-param-style :comma-separated
                     :query-params
                     {:sportId SPORT_ID_MLB
                      :date date
                      :leagueId [LEAGUE_ID_AL LEAGUE_ID_NL]
                      :hydrate ["team" "probablePitcher(note)"]
                      :language "en"}})]
     (future (let [data (->> @schedules :body :dates (filter #(= date (:date %))) first)]
               (->> data
                    :games
                    (map (fn [game]
                           {:game-id (:gamePk game)
                            :probable-pitchers [(probable-pitcher-data-from-game game :home)
                                                (probable-pitcher-data-from-game game :away)]}))
                    (into [])))))))
