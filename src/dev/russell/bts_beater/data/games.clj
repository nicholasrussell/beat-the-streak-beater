(ns dev.russell.bts-beater.data.games
  (:require [clojure.spec.alpha :as s]
            [dev.russell.bts-beater.mlb-api.game.core :as api-game]
            [dev.russell.bts-beater.mlb-api.schedule.core :as api-schedule]
            [dev.russell.bts-beater.mlb-api.team.core :as api-team]
            [dev.russell.bts-beater.util.date.core :as date-util]
            [dev.russell.bts-beater.util.date.spec :as date-util-spec]
            [cheshire.core :as cheshire]
            [clojure.string :as string])
  (:import (java.time LocalDate)))

(s/def ::game-id (s/or :string string?
                       :integer pos-int?))

(defn game-id
  [data]
  (some-> data :gamePk str))

(defn- transform-yes-no
  [yn]
  (= (string/lower-case yn) "y"))

(defn- transform-schedule-to-game-ids
  [schedule-data]
  (some->> schedule-data :dates first :games (map game-id)))

(defn- transform-game-data-meta
  [game]
  {:id (some-> game :id)
   :type (some-> game :type)
   :gameday-type (some-> game :gamedayType)
   :season (some-> game :season)
   :game-number (some-> game :gameNumber)
   :tiebreaker? (some-> game :tiebreaker transform-yes-no)
   :double-header? (some-> game :doubleHeader transform-yes-no)
   :calendar-event-id (some-> game :calendarEventID)})

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
              :state-abbrev (some-> venue :location :stateAbbrev)
              :coordinates (some-> venue :location :defaultCoordinates)}
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
     :name (:name team)
     :record (:record team)}))

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
  (println (cheshire/generate-string (-> game :gameData :teams) {:pretty true}))
  (let [game-data (:gameData game)
        team-data (some-> game-data :teams transform-game-data-teams)
        rosters (some-> team-data transform-team-data-rosters)
        players (some-> game-data :players transform-game-data-players)
        roster-players (join-game-data-rosters-players rosters players)]
    {:id (game-id game)
     :meta (some-> game-data :game transform-game-data-meta)
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
(s/fdef get-games
        :args (s/alt :default-date (s/cat)
                     :specific-date (s/cat :date ::date-util-spec/iso-date))
        :ret (s/nilable (s/coll-of ::game-id)))

(defn get-game
  [game-id]
  (transform-game-data (api-game/get-live-feed game-id)))
(s/fdef get-game
        :args (s/cat :game-id ::game-id))

