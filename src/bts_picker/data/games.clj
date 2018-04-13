(ns bts-picker.data.games
  (:require [bts-picker.mlb-api.game.core :as api-game]
            [bts-picker.mlb-api.schedule.core :as api-schedule]
            [bts-picker.mlb-api.team.core :as api-team]
            [bts-picker.util.date :as date-util]))

(defn game-id
  [data]
  (some-> data :gamePk str))

(defn- transform-schedule-to-game-ids
  [schedule-data]
  (some->> schedule-data :dates first :games (map game-id)))

(defn- transform-game-data-status
  [status]
  {:code (:abstractGameCode status)
   :detail (:detailedState status)})

(defn- transform-game-data-venue
  [venue]
  {:id (some-> venue :id str)
   :name (:name venue)
   :location {:city (some-> venue :location :city)
              :state (some-> venue :location :state)
              :state-abbrev (some-> venue :location :stateAbbrev)}
   :timezone (some-> venue :timeZone)})

(defn- transform-game-data-weather
  [weather]
  {:condition (:condition weather)
   :temperature-f (:temp weather)
   :wind (:wind weather)})

(defn- transform-game-data-probable-pitchers
  [probable-pitchers]
  {:home (some-> probable-pitchers :home :id str)
   :away (some-> probable-pitchers :away :id str)})

(defn- transform-game-data-team
  [team]
  (let [team (select-keys team [:id :name])]
    {:id (some-> team :id str)
     :name (:name team)}))

(defn- transform-game-data-teams
  [teams]
  {:home (some-> teams :home transform-game-data-team)
   :away (some-> teams :away transform-game-data-team)})

(defn- transform-game-data-player
  [player]
  {:id (some-> player :id str)
   :name (:fullName player)})

(defn- transform-game-data-players
  [players]
  (map transform-game-data-player (some->> players vals (filter :active))))

(defn- transform-game-data-roster
  [roster]
  (map
   (fn [person]
     {:player-id (some-> person :person :id str)
      :jersey-number (:jerseyNumber person)})
   (:roster roster)))

(defn- transform-team-data-rosters
  [teams]
  {:home (some-> teams :home :id api-team/get-roster transform-game-data-roster)
   :away (some-> teams :away :id api-team/get-roster transform-game-data-roster)})

(defn- join-game-data-rosters-players
  [rosters players]
  (reduce-kv
   (fn [acc team roster]
     (assoc acc
       team (remove
             nil?
             (map (fn [roster-player]
                    (when-let [p (->> players (filter #(= (:id %) (:player-id roster-player))) first)]
                      (merge p (dissoc roster-player :player-id))))
                  roster))))
   {}
   rosters))

(defn- join-game-data-team-players
  [teams players]
  {:home (assoc (:home teams) :players (into [] (:home players)))
   :away (assoc (:away teams) :players (into [] (:away players)))})

(defn- transform-game-data
  [game]
  (let [game-data (:gameData game)
        team-data (some-> game-data :teams transform-game-data-teams)
        rosters (some-> team-data transform-team-data-rosters)
        players (some-> game-data :players transform-game-data-players)
        roster-players (join-game-data-rosters-players rosters players)]
    {:id (game-id game)
     :date (some-> game-data :datetime :dateTime)
     :status (some-> game-data :status transform-game-data-status)
     :venue (some-> game-data :venue transform-game-data-venue)
     :weather (some-> game-data :weather transform-game-data-weather)
     :probable-pitchers (some-> game-data :probablePitchers transform-game-data-probable-pitchers)
     :teams (join-game-data-team-players team-data roster-players)}))

(defn get-games
  ([]
   (get-games (date-util/now)))
  ([date]
   (transform-schedule-to-game-ids (api-schedule/get-schedule {:date (date-util/format-date date)}))))

(defn get-game
  [game-id]
  (transform-game-data (api-game/get-live-feed game-id)))
