(ns bts-picker.mlb-api.config.core
  (:require [bts-picker.mlb-api.client.core :as client]
            [clojure.spec.alpha :as s]))

(def ^:private path-baseball-stats "/v1/baseballStats")
(def ^:private path-event-types "/v1/eventTypes")
(def ^:private path-game-statuses "/v1/gameStatus")
(def ^:private path-game-types "/v1/gameTypes")
(def ^:private path-hit-trajectories "/v1/hitTrajectories")
(def ^:private path-job-types "/v1/jobTypes")
(def ^:private path-languages "/v1/languages")
(def ^:private path-league-leader-types "/v1/leagueLeaderTypes")
(def ^:private path-logical-events "/v1/logicalEvents")
(def ^:private path-metrics "/v1/metrics")
(def ^:private path-pitch-codes "/v1/pitchCodes")
(def ^:private path-pitch-types "/v1/pitchTypes")
(def ^:private path-platforms "/v1/platforms")
(def ^:private path-positions "/v1/positions")
(def ^:private path-review-reasons "/v1/reviewReasons")
(def ^:private path-roster-types "/v1/rosterTypes")
(def ^:private path-schedule-event-types "/v1/scheduleEventTypes")
(def ^:private path-situation-codes "/v1/situationCodes")
(def ^:private path-skies "/v1/sky")
(def ^:private path-standings-types "/v1/standingsTypes")
(def ^:private path-stat-groups "/v1/statGroups")
(def ^:private path-stat-types "/v1/statTypes")
(def ^:private path-wind-directions "/v1/windDirection")

(defn get-baseball-stats
  []
  (client/get path-baseball-stats))

(defn get-event-types
  []
  (client/get path-event-types))

(defn get-game-statuses
  []
  (client/get path-game-statuses))

(defn get-game-types
  []
  (client/get path-game-types))

(defn get-hit-trajectories
  []
  (client/get path-hit-trajectories))

(defn get-job-types
  []
  (client/get path-job-types))

(defn get-languages
  []
  (client/get path-languages))

(defn get-league-leader-types
  []
  (client/get path-league-leader-types))

(defn get-logical-events
  []
  (client/get path-logical-events))

(defn get-metrics
  []
  (client/get path-metrics))

(defn get-pitch-codes
  []
  (client/get path-pitch-codes))

(defn get-pitch-types
  []
  (client/get path-pitch-types))

(defn get-platforms
  []
  (client/get path-platforms))

(defn get-positions
  []
  (client/get path-positions))

(defn get-review-reasons
  []
  (client/get path-review-reasons))

(defn get-roster-types
  []
  (client/get path-roster-types))

(defn get-schedule-event-types
  []
  (client/get path-schedule-event-types))

(defn get-situation-codes
  []
  (client/get path-situation-codes))

(defn get-skies
  []
  (client/get path-skies))

(defn get-standings-types
  []
  (client/get path-standings-types))

(defn get-stat-groups
  []
  (client/get path-stat-groups))

(defn get-stat-types
  []
  (client/get path-stat-types))

(defn get-wind-directions
  []
  (client/get path-wind-directions))

; (->> (get-game-statuses) (map :abstractGameCode) (into #{}))

(s/def :stat-group/displayName string?)
(s/def ::stat-group (s/keys :req-un [:stat-group/displayName]))

(s/def :baseball-stat/name string?)
(s/def :baseball-stat/lookupParam string?)
(s/def :baseball-stat/isCounting boolean?)
(s/def :baseball-stat/statGroups (s/coll-of ::stat-group))
(s/def :baseball-stat/orgTypes (s/coll-of any?))
(s/def ::baseball-stat (s/keys :req-un [:baseball-stat/name
                                        :baseball-stat/lookupParam
                                        :baseball-stat/isCounting
                                        :baseball-stat/statGroups
                                        :baseball-stat/orgTypes]))

(s/def :event-type/code string?)
(s/def :event-type/description string?)
(s/def ::event-type (s/keys :req-un [:event-type/code
                                     :event-type/description]))

(s/def :game-status/abstractGameCode string?)
(s/def :game-status/abstractGameState string?)
(s/def :game-status/codedGameState string?)
(s/def :game-status/statusCode string?)
(s/def :game-status/reason string?)
(s/def :game-status/detailedState string?)
(s/def ::game-status (s/keys :req-un [:game-status/abstractGameCode
                                      :game-status/abstractGameState
                                      :game-status/codedGameState
                                      :game-status/detailedState
                                      :game-status/statusCode]
                             :opt-un [:game-status/reason]))

(s/def :game-type/id string?)
(s/def :game-type/description string?)
(s/def ::game-type (s/keys :req-un [:game-type/id
                                    :game-type/description]))

(s/def :hit-trajectory/code string?)
(s/def :hit-trajectory/description string?)
(s/def ::hit-trajectory (s/keys :req-un [:hit-trajectory/code
                                         :hit-trajectory/description]))

(s/def :job-type/code string?)
(s/def :job-type/job string?)
(s/def :job-type/sortOrder int?)
(s/def ::job-type (s/keys :req-un [:job-type/code
                                   :job-type/job]
                          :opt-un [:job-type/sortOrder]))

(s/def :language/name string?)
(s/def :language/languageCode string?)
(s/def :language/locale string?)
(s/def ::language (s/keys :req-un [:language/name
                                   :language/languageCode
                                   :language/locale]))

(s/def :logical-event/code string?)
(s/def ::logical-event (s/keys :req-un [:logical-event/code]))

(s/def :metric/group string?) ; TODO comma-separated :stat-group/displayName's
(s/def :metric/name string?)
(s/def :metric/unit string?)
(s/def :metric/metricId int?)
(s/def ::metric (s/keys :req-un [:metric/group
                                 :metric/name
                                 :metric/unit
                                 :metric/metricId]))

(s/def :pitch-code/code string?)
(s/def :pitch-code/description string?)
(s/def ::pitch-code (s/keys :req-un [:pitch-code/code
                                     :pitch-code/description]))

(s/def :pitch-type/code string?)
(s/def :pitch-type/description string?)
(s/def ::pitch-type (s/keys :req-un [:pitch-type/code
                                     :pitch-type/description]))

(s/def :platform/platformCode string?)
(s/def :platform/platformDescription string?)
(s/def ::platform (s/keys :req-un [:platform/platformCode
                                   :platform/platformDescription]))

(s/def :position/code string?)
(s/def :position/type string?)
(s/def :position/abbrev string?)
(s/def :position/formalName string?)
(s/def :position/shortName string?)
(s/def :position/fullName string?)
(s/def :position/displayName string?)
(s/def :position/pitcher boolean?)
(s/def :position/fielder boolean?)
(s/def :position/outfield boolean?)
(s/def ::position (s/keys :req-un [:position/code
                                   :position/type
                                   :position/abbrev
                                   :position/formalName
                                   :position/shortName
                                   :position/fullName
                                   :position/displayName
                                   :position/pitcher
                                   :position/fielder
                                   :position/outfield]))

(s/def :review-reason/code string?)
(s/def :review-reason/description string?)
(s/def ::review-reason (s/keys :req-un [:review-reason/code
                                        :review-reason/description]))

(s/def :roster-type/description string?)
(s/def :roster-type/lookupName string?)
(s/def :roster-type/parameter string?)
(s/def ::roster-type (s/keys :req-un [:roster-type/description
                                      :roster-type/lookupName
                                      :roster-type/parameter]))

(s/def :schedule-event-type/code string?)
(s/def :schedule-event-type/name string?)
(s/def ::schedule-event-type (s/keys :req-un [:schedule-event-type/code
                                              :schedule-event-type/name]))

(s/def :situation-code/code string?)
(s/def :situation-code/sortOrder int?) ; TODO sort order type?
(s/def :situation-code/navigationMenu string?)
(s/def :situation-code/description string?)
(s/def :situation-code/team boolean?)
(s/def :situation-code/batting boolean?)
(s/def :situation-code/fielding boolean?)
(s/def :situation-code/pitching boolean?)
(s/def ::situation-code (s/keys :req-un [:situation-code/code
                                         :situation-code/sortOrder
                                         :situation-code/navigationMenu
                                         :situation-code/description
                                         :situation-code/team
                                         :situation-code/batting
                                         :situation-code/fielding
                                         :situation-code/pitching]))

(s/def :sky/code string?)
(s/def :sky/description string?)
(s/def ::sky (s/keys :req-un [:sky/code
                              :sky/description]))

(s/def :standings-type/name string?)
(s/def :standings-type/description string?)
(s/def ::standings-type (s/keys :req-un [:standings-type/name
                                         :standings-type/description]))

(s/def :wind-direction/code string?)
(s/def :wind-direction/description string?)
(s/def ::wind-direction (s/keys :req-un [:wind-direction/code
                                         :wind-direction/description]))

(s/def :stat-type/displayName string?)
(s/def ::stat-type (s/keys :req-un [:stat-type/displayName]))

(s/def ::league-leader-type (s/keys :req-un [])) ; TODO

(s/fdef get-baseball-stats
  :args (s/cat)
  :ret (s/coll-of ::baseball-stat))

(s/fdef get-event-types
  :args (s/cat)
  :ret (s/coll-of ::event-type))

(s/fdef get-game-statuses
  :args (s/cat)
  :ret (s/coll-of ::game-status))

(s/fdef get-game-types
  :args (s/cat)
  :ret (s/coll-of ::game-type))

(s/fdef get-hit-trajectories
  :args (s/cat)
  :ret (s/coll-of ::hit-trajectory))

(s/fdef get-job-types
  :args (s/cat)
  :ret (s/coll-of ::job-type))

(s/fdef get-languages
  :args (s/cat)
  :ret (s/coll-of ::language))

(s/fdef get-league-leader-types
  :args (s/cat)
  :ret (s/coll-of ::league-leader-type))

(s/fdef get-logical-events
  :args (s/cat)
  :ret (s/coll-of ::logical-event))

(s/fdef get-metrics
  :args (s/cat)
  :ret (s/coll-of ::metric))

(s/fdef get-pitch-codes
  :args (s/cat)
  :ret (s/coll-of ::pitch-code))

(s/fdef get-pitch-types
  :args (s/cat)
  :ret (s/coll-of ::pitch-type))

(s/fdef get-platforms
  :args (s/cat)
  :ret (s/coll-of ::platforms))

(s/fdef get-positions
  :args (s/cat)
  :ret (s/coll-of ::positions))

(s/fdef get-review-reasons
  :args (s/cat)
  :ret (s/coll-of ::review-reason))

(s/fdef get-roster-types
  :args (s/cat)
  :ret (s/coll-of ::roster-type))

(s/fdef get-schedule-event-types
  :args (s/cat)
  :ret (s/coll-of ::scheduled-event-type))

(s/fdef get-situation-codes
  :args (s/cat)
  :ret (s/coll-of ::situation-code))

(s/fdef get-skies
  :args (s/cat)
  :ret (s/coll-of ::sky))

(s/fdef get-standings-types
  :args (s/cat)
  :ret (s/coll-of ::standings-type))

(s/fdef get-stat-groups
  :args (s/cat)
  :ret (s/coll-of ::stat-group))

(s/fdef get-stat-types
  :args (s/cat)
  :ret (s/coll-of ::stat-type))

(s/fdef get-wind-directions
  :args (s/cat)
  :ret (s/coll-of ::wind-direction))

