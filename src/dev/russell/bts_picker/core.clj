(ns dev.russell.bts-picker.core
  (:require [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [clojure.tools.trace :as trace]
            [dev.russell.bts-picker.config :as config]
            [dev.russell.bts-picker.db.seed :as seed]
            [dev.russell.bts-picker.util.date.core :as date]
            [dev.russell.bts-picker.rankings.batter :as batter-ranking]
            [dev.russell.bts-picker.rankings.batter-vs-pitcher :as batter-vs-pitcher-ranking]
            [dev.russell.bts-picker.rankings.pitcher :as pitcher-ranking]
            [dev.russell.bts-picker.rankings.rank :as rank]
            [dev.russell.bts-picker.db.models.game :as games]
            [dev.russell.bts-picker.db.models.player :as players]
            [dev.russell.bts-picker.db.models.probable-pitcher :as probable-pitchers]
            [dev.russell.bts-picker.db.models.roster :as rosters]
            [dev.russell.bts-picker.db.models.season :as season]
            [dev.russell.bts-picker.db.models.team :as teams]
            [dev.russell.bts-picker.db.core :as db]))

(defn- print-picks
  [ds picks]
  ;; TODO join this data at the db level
  (let [pick-data (pmap
                   (fn [pick]
                     (let [player (players/get-by-id ds (:player-id pick))
                           roster (rosters/get-by-player-id ds (:player-id pick))
                           team (teams/get-by-id ds (:team-id roster))]
                       {:player-name (:full-name player)
                        :team-name (:name team)
                        :score (:score pick)})) 
                   picks)]
    (pprint/print-table [:player-name :team-name :score] pick-data)))

(def cli-options
  [["-d" "--date DATE" "Date of games"
    :id :date
    :default (str (date/now))
    :validate [#(re-matches #"\d{4}-\d{2}-\d{2}" %) "Must be ISO 8601 date"]]
   ["-f" "--force" "Force refresh data"
    :id :force
    :default false
    :parse-fn (constantly true)]])

(defn -main
  [& args]
  (let [opts (cli/parse-opts args cli-options)]
    (when (:errors opts)
      (throw (IllegalArgumentException. (string/join "\n" (:errors opts)))))
    (config/initialize (read-string (slurp "conf/bts-picker.edn")))
    (let [date (-> opts :options :date)
          ds (db/get-datasource)]
      (when (or (-> opts :options :force) (empty? (games/get-by-date ds date)))
        (seed/seed-daily date))
      (let [current-season (season/get-current-id ds) ; TODO get from date
            games (games/get-by-date ds date)
            matchups (games/get-matchups-by-date ds date)
            batter-scores (->> (vals matchups)
                               (mapcat
                                (fn [matchup]
                                  (concat (-> matchup :home :roster-ids)
                                          (-> matchup :away :roster-ids))))
                               (batter-ranking/score-batters current-season))
            pitcher-scores (->> (vals matchups)
                                (mapcat
                                 (fn [matchup]
                                   (concat [(-> matchup :home :probable-pitcher-id)]
                                           [(-> matchup :away :probable-pitcher-id)])))
                                (remove nil?)
                                (pitcher-ranking/score-pitchers current-season))
            batter-vs-pitcher-scores (batter-vs-pitcher-ranking/score-batters-vs-pitchers (vals matchups))
            rankings (rank/rank-batters (vals matchups) batter-scores batter-vs-pitcher-scores pitcher-scores)]
        (print-picks ds (take 10 rankings))))))
