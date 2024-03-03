(ns dev.russell.bts-picker.core
  (:require [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [dev.russell.bts-picker.config :as config]
            [dev.russell.batboy.schedules.core :as schedule]
            [dev.russell.bts-picker.db.seed :as seed]
            [dev.russell.batboy.games.core :as game]
            [dev.russell.batboy.people.core :as people]
            [dev.russell.batboy.stats.core :as stats]
            [dev.russell.batboy.teams.core :as team]
            [dev.russell.bts-picker.util.date.core :as date]
            [dev.russell.bts-picker.rankings.batter :as batter-ranking]
            [dev.russell.bts-picker.rankings.batter-vs-pitcher :as batter-vs-pitcher-ranking]
            [dev.russell.bts-picker.rankings.pitcher :as pitcher-ranking]
            [dev.russell.bts-picker.rankings.rank :as rank]))

(defn schedule
  ([] (schedule (str (date/now))))
  ([date]
   (let [schedules (schedule/get-schedules
                    {:multi-param-style :comma-separated
                     :query-params
                     {:sportId 1
                      :date date
                      :leagueId [103 104]
                      :hydrate ["team" "probablePitcher(note)"]
                      :language "en"}})]
     (future (->> @schedules :body :dates (filter #(= date (:date %))) first)))))

(defn games
  ([] (games (schedule)))
  ([schedule]
   (future
     (->> @schedule
          :games
          (map (fn [game]
                 {:game-id (:gamePk game)
                  :teams {:away-team-id (-> game :teams :away :team :id)
                          :home-team-id (-> game :teams :home :team :id)}}))))))

(defn- probable-pitcher-data-from-game
  [game side]
  (let [id (-> game :teams side :probablePitcher :id)
        team-id (-> game :teams side :team :id)]
    {:player-id id
     :game-id (:gamePk game)
     :team-id team-id}))

(defn probable-pitchers
  ([] (probable-pitchers (schedule)))
  ([schedule]
   (future
     (->> @schedule
          :games
          (mapcat (fn [game]
                    [(probable-pitcher-data-from-game game :home)
                     (probable-pitcher-data-from-game game :away)]))))))

(defn- roster-data-from-game
  [game side]
  (let [team-id (-> game :teams side :team :id)]
    (future
      {:team-id team-id
       :team {:id team-id
              :name (-> game :teams side :team :name)}
       :roster (->> @(team/get-team-roster {:path-params {:id team-id}})
                    :body
                    :roster
                    (map (fn [player]
                           {:player-id (:id (:person player))
                            :name (:fullName (:person player))
                            :position (:abbreviation (:position player))
                            :status (:code (:status player))})))})))

(defn rosters
  ([] (rosters (schedule)))
  ([schedule]
   (future
     (->> @schedule
          :games
          (mapcat (fn [game]
                    [(roster-data-from-game game :home)
                     (roster-data-from-game game :away)]))
          (pmap (fn [roster-promise] @roster-promise))))))

(defn matchups
  [schedule rosters probable-pitchers]
  (future
    (let [game-matchups (->> @schedule
                             :games
                             (map (fn [game]
                                    {:game-id (:gamePk game)
                                     :away-team-id (-> game :teams :away :team :id)
                                     :home-team-id (-> game :teams :home :team :id)})))]
      (reduce
       (fn [acc game-matchup]
         (let [home-pitcher (first (filter #(= (:team-id %) (:home-team-id game-matchup)) @probable-pitchers))
               away-pitcher (first (filter #(= (:team-id %) (:away-team-id game-matchup)) @probable-pitchers))
               home-roster (first (filter #(= (:team-id %) (:home-team-id game-matchup)) @rosters))
               away-roster (first (filter #(= (:team-id %) (:away-team-id game-matchup)) @rosters))]
           (conj
            acc
            {:game-id (:game-id game-matchup)
             :teams {:away {:team-id (:team-id away-roster)
                            :pitcher away-pitcher
                            :roster (:roster away-roster)}
                     :home {:team-id (:team-id home-roster)
                            :pitcher home-pitcher
                            :roster (:roster home-roster)}}})))
       []
       game-matchups))))

(defn- print-picks
  [rosters ranked-batters]
  (let [decorated-batters (pmap (fn [batter]
                                  (let [roster (reduce
                                                (fn [_ cur]
                                                  (when-let [player (->> cur :roster (filter #(= (:player-id %) (:player-id batter))) first)]
                                                     (reduced {:player-name (:name player)
                                                               :team-name (:name (:team cur))})))
                                                nil
                                                @rosters)]
                                    (merge batter roster)))
                                ranked-batters)]
    (pprint/print-table [:player-name :team-name :score] decorated-batters)))

(def cli-options
  [["-d" "--date DATE" "Date"
    :default (str (date/now))
    :validate [#(re-matches #"\d{4}-\d{2}-\d{2}" %) "Must be ISO 8601 date"]]])

(defn -main
  [& args]
  (let [opts (cli/parse-opts args cli-options)]
    (when (:errors opts)
      (throw (IllegalArgumentException. (string/join "\n" (:errors opts)))))
    (config/initialize (read-string (slurp "conf/bts-picker.edn")))
    (let [date (-> opts :options :date)
          schedule (schedule date)
          probable-pitchers (probable-pitchers schedule)
          rosters (rosters schedule)
          matchups (matchups schedule rosters probable-pitchers)
          batter-scores (->> @rosters
                             (mapcat (fn [roster] (map :player-id (:roster roster))))
                             batter-ranking/score-batters
                             (map deref)
                             (into []))
          batter-vs-pitcher-scores (->>
                                    @(batter-vs-pitcher-ranking/score-batters-vs-pitchers matchups)
                                    (map deref)
                                    (into []))
          pitcher-scores (->> (pitcher-ranking/score-pitchers (map :player-id @probable-pitchers))
                              (map deref)
                              (into []))]
      (print-picks rosters (take 10 (rank/rank-batters matchups batter-scores batter-vs-pitcher-scores pitcher-scores))))))
