(ns bts-picker.data.api
  (:require [bts-picker.util.date.core :as date-util]
            [bts-picker.mlb-api.schedule.core :as api-schedule]
            [bts-picker.mlb-api.game.core :as api-game]
            [bts-picker.mlb-api.team.core :as api-team]
            [bts-picker.mlb-api.person.core :as api-person]))

(defn get-schedule
  ([]
   (get-schedule (date-util/now)))
  ([date]
   (-> (api-schedule/get-schedule {:date (date-util/format-date date) :sport-id 1}) :body)))

(defn get-game
  [game-id]
  (-> (api-game/get-live-feed game-id) :body))

(defn- transform-game-id
  [game-data]
  (:game-pk game-data))

(defn- transform-game-meta
  [game-data]
  (let [game (:game game-data)
        status (:status game-data)]
    (merge
     (select-keys game [:id :pk :type :season :game-number :tiebreaker? :double-header?])
     {:status {:code (:status-code status)
               :detail (:detailed-state status)}})))

(defn- transform-game-date
  [game-data]
  (some-> game-data :datetime :date-time))

(defn- transform-game-venue
  [game-data]
  (let [venue (:venue game-data)]
    {:id (:id venue)
     :name (:name venue)
     :location {:city (-> venue :location :city)
                :state (-> venue :location :state)
                :state-abbrev (-> venue :location :state-abbrev)
                :coordinates (-> venue :location :default-coordinates)}
     :time-zone (:time-zone venue)}))

(defn- transform-game-weather
  [game-data]
  (let [weather (:weather game-data)]
    {:condition (:condition weather)
     :temperature {:value (:temp weather)
                   :units :f}
     :wind (:wind weather)}))

(defn- transform-game-probable-pitchers
  [game-data]
  (let [probable-pitchers (:probable-pitchers game-data)]
    {:home (some-> probable-pitchers :home :id)
     :away (some-> probable-pitchers :away :id)}))

(defn- transform-team
  [team]
  {:id (:id team)
   :code (:team-code team)
   :name (:name team)
   :abbreviation (:abbreviation team)
   :league {:id (:id (:league team))}
   :venue {:id (:id (:venue team))}
   :record (select-keys (:record team) [:division-leader? :league-games-back :sport-games-back :division-games-back :conference-games-back :wild-card-games-back :league-record :games-played :wins :losses :winning-percentage])})

(defn- transform-team-data
  [game-data]
  (let [teams (:teams game-data)]
    {:home (some-> teams :home transform-team)
     :away (some-> teams :away transform-team)}))

(defn- transform-roster
  [roster]
  (mapv
   (fn [person]
     {:player-id (some-> person :person :id)})
   (:roster roster)))

(defn- transform-rosters
  [team-data]
  {:home (some-> team-data :home :id api-team/get-roster :body transform-roster)
   :away (some-> team-data :away :id api-team/get-roster :body transform-roster)})

(defn- transform-game-data-player
  [player]
  {:id (:id player)
   :name (:full-name player)})

(defn- transform-game-data-players
  [game-data]
  (mapv transform-game-data-player (some->> game-data :players vals (filter :active?))))

(defn- join-game-data-rosters-players
  [rosters players]
  (reduce-kv
   (fn [acc team roster]
     (assoc acc
            team (into []
                       (remove
                        nil?
                        (map (fn [roster-player]
                               (when-let [p (->> players (filter #(= (:id %) (:player-id roster-player))) first)]
                                 (merge p (dissoc roster-player :player-id))))
                             roster)))))
   {}
   rosters))

(defn transform-game-data
  [game]
  (let [game-data (:game-data game)
        team-data (some-> game-data transform-team-data)
        rosters (some-> team-data transform-rosters)
        game-players (some-> game-data transform-game-data-players)
        roster-players (join-game-data-rosters-players rosters game-players)]
    {:id (some-> game-data :game :pk)
     :meta (some-> game-data transform-game-meta)
     :date-time (some-> game-data transform-game-date)
     :venue (some-> game-data transform-game-venue)
     :weather (some-> game-data transform-game-weather)
     :probable-pitchers (some-> game-data transform-game-probable-pitchers)
     :teams {:home (merge (:home team-data)
                          {:players (:home roster-players)})
             :away (merge (:away team-data)
                          {:players (:away roster-players)})}}))

(defn- transform-player-data
  [player]
  {:id (:id player)
   :name (:full-name player)
   :position (:primary-position player)
   :bat-side (:bat-side player)
   :birth-date (:birth-date player)
   :pitch-hand (:pitch-hand player)
   :strike-zone {:top (:strike-zone-top player)
                 :bottom (:strike-zone-bottom player)}
   :weight (:weight player)
   :height (:height player)})

(defn get-players
  [player-ids]
  (->>
   player-ids
   api-person/get-people
   :body
   :people
   (filter :active?)
   (filter :is-player?)
   (mapv transform-player-data)))

(defn get-todays-players
  []
  (let [game-data (transform-game-data (get-game 616620))]
    (merge game-data
           {:players (get-players (map :id (concat (-> game-data :teams :home :players)
                                                   (-> game-data :teams :away :players))))})))

                                        ;624414 

; stat types
#_({:display-name "projected"}
  {:display-name "projectedRos"}
  {:display-name "yearByYear"}
  {:display-name "yearByYearAdvanced"}
  {:display-name "season"}
  {:display-name "career"}
  {:display-name "seasonAdvanced"}
  {:display-name "careerStatSplits"}
  {:display-name "gameLog"}
  {:display-name "playLog"}
  {:display-name "pitchLog"}
  {:display-name "metricLog"}
  {:display-name "metricAverages"}
  {:display-name "pitchArsenal"}
  {:display-name "infieldOutsAboveAverage"}
  {:display-name "outsAboveAverage"}
  {:display-name "expectedStatistics"}
  {:display-name "catcherFraming"}
  {:display-name "shifts"}
  {:display-name "sprayChart"}
  {:display-name "tracking"}
  {:display-name "vsPlayer"}
  {:display-name "vsPlayerTotal"}
  {:display-name "vsPlayer5Y"}
  {:display-name "vsTeam"}
  {:display-name "vsTeam5Y"}
  {:display-name "vsTeamTotal"}
  {:display-name "lastXGames"}
  {:display-name "byDateRange"}
  {:display-name "byDateRangeAdvanced"}
  {:display-name "byMonth"}
  {:display-name "byDayOfWeek"}
  {:display-name "rankings"}
  {:display-name "rankingsByYear"}
  {:display-name "statsSingleSeason"}
  {:display-name "statsSingleSeasonAdvanced"}
  {:display-name "hotColdZones"}
  {:display-name "availableStats"}
  {:display-name "opponentsFaced"}
  {:display-name "statSplits"}
  {:display-name "statSplitsAdvanced"}
  {:display-name "atGameStart"}
   {:display-name "vsOpponents"})

                                        ; stat groups
#_({:display-name "hitting"}
   {:display-name "pitching"}
   {:display-name "fielding"}
   {:display-name "catching"}
   {:display-name "running"}
   {:display-name "game"}
   {:display-name "team"}
   {:display-name "streak"})
