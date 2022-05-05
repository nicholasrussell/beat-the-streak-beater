(ns dev.russell.bts-picker.db.seed
  (:require [clojure.string :as string]
            [taoensso.timbre :as log]
            [dev.russell.batboy.divisions.core :as api-divisions]
            [dev.russell.batboy.leagues.core :as api-leagues]
            [dev.russell.batboy.meta.core :as api-meta]
            [dev.russell.batboy.people.core :as api-people]
            [dev.russell.batboy.seasons.core :as api-seasons]
            [dev.russell.batboy.sports.core :as api-sports]
            [dev.russell.batboy.teams.core :as api-teams]
            [dev.russell.batboy.venues.core :as api-venues]
            [dev.russell.bts-picker.constants :refer [SPORT_CODE_MLB LEAGUE_CODE_AL LEAGUE_CODE_NL]]
            [dev.russell.bts-picker.db.core :as db]
            [dev.russell.bts-picker.db.models.division :as division]
            [dev.russell.bts-picker.db.models.game-type :as game-type]
            [dev.russell.bts-picker.db.models.hit-trajectory :as hit-trajectory]
            [dev.russell.bts-picker.db.models.league :as league]
            [dev.russell.bts-picker.db.models.metric :as metric]
            [dev.russell.bts-picker.db.models.pitch-code :as pitch-code]
            [dev.russell.bts-picker.db.models.pitch-type :as pitch-type]
            [dev.russell.bts-picker.db.models.player :as player]
            [dev.russell.bts-picker.db.models.player-stats :as player-stats]
            [dev.russell.bts-picker.db.models.position :as position]
            [dev.russell.bts-picker.db.models.roster :as roster]
            [dev.russell.bts-picker.db.models.roster-type :as roster-type]
            [dev.russell.bts-picker.db.models.season :as season]
            [dev.russell.bts-picker.db.models.sky :as sky]
            [dev.russell.bts-picker.db.models.sport :as sport]
            [dev.russell.bts-picker.db.models.standing-type :as standing-type]
            [dev.russell.bts-picker.db.models.stat-group :as stat-group]
            [dev.russell.bts-picker.db.models.stat-type :as stat-type]
            [dev.russell.bts-picker.db.models.team :as team]
            [dev.russell.bts-picker.db.models.venue :as venue]
            [dev.russell.bts-picker.db.models.wind :as wind])
  (:import [java.time LocalDate]))

(defn seed-game-types
  [ds]
  (log/debug :seed/starting :game-types)
  (->> @(api-meta/get-game-types {})
       :body
       (map (partial game-type/upsert ds))
       doall)
  (log/debug :seed/finished :game-types))

(defn seed-hit-trajectories
  [ds]
  (log/debug :seed/starting :hit-trajectories)
  (->> @(api-meta/get-hit-trajectories {})
       :body
       (map (partial hit-trajectory/upsert ds))
       doall)
  (log/debug :seed/finished :hit-trajectories))

(defn seed-pitch-codes
  [ds]
  (log/debug :seed/starting :pitch-codes)
  (->> @(api-meta/get-pitch-codes {})
       :body
       (map (partial pitch-code/upsert ds))
       doall)
  (log/debug :seed/finished :pitch-codes))

(defn seed-pitch-types
  [ds]
  (log/debug :seed/starting :pitch-types)
  (->> @(api-meta/get-pitch-types {})
       :body
       (map (partial pitch-type/upsert ds))
       doall)
  (log/debug :seed/finished :pitch-types))

(defn seed-positions
  [ds]
  (log/debug :seed/starting :positions)
  (->> @(api-meta/get-positions {})
       :body
       (map (fn [position]
              {:code (:code position)
               :abbreviation (:abbrev position)
               :display-name (:displayName position)
               :type (:type position)
               :game-position (:gamePosition position)
               :fielder (:fielder position)
               :outfield (:outfield position)
               :pitcher (:pitcher position)}))
       (map (partial position/upsert ds))
       doall)
  (log/debug :seed/finished :positions))

(defn seed-roster-types
  [ds]
  (log/debug :seed/starting :roster-types)
  (->> @(api-meta/get-roster-types {})
       :body
       (map (fn [roster-type]
              {:parameter (:parameter roster-type)
               :description (:description roster-type)
               :lookup-name (:lookupName roster-type)}))
       (map (partial roster-type/upsert ds))
       doall)
  (log/debug :seed/finished :roster-types))

(defn seed-skies
  [ds]
  (log/debug :seed/starting :skies)
  (->> @(api-meta/get-sky {})
       :body
       (map (partial sky/upsert ds))
       doall)
  (log/debug :seed/finished :skies))

(defn seed-standing-types
  [ds]
  (log/debug :seed/starting :standing-types)
  (->> @(api-meta/get-standings-types {})
       :body
       (map (partial standing-type/upsert ds))
       doall)
  (log/debug :seed/finished :standing-types))

(defn seed-winds
  [ds]
  (log/debug :seed/starting :winds)
  (->> @(api-meta/get-wind-direction {})
       :body
       (map (partial wind/upsert ds))
       doall)
  (log/debug :seed/finished :winds))

(defn seed-stat-groups
  [ds]
  (log/debug :seed/starting :stat-groups)
  (->> @(api-meta/get-stat-groups {})
       :body
       (map (fn [stat-group]
              {:code (:displayName stat-group)}))
       (map (partial stat-group/upsert ds))
       doall)
  (log/debug :seed/finished :stat-groups))

(defn seed-stat-types
  [ds]
  (log/debug :seed/starting :stat-types)
  (->> @(api-meta/get-stat-types {})
       :body
       (map (fn [stat-type]
              {:code (:displayName stat-type)}))
       (map (partial stat-type/upsert ds))
       doall)
  (log/debug :seed/finished :stat-types))

(defn seed-metrics
  [ds]
  (log/debug :seed/starting :metrics)
  (->> @(api-meta/get-metrics {})
       :body
       (filter #(not (string/blank? (:name %))))
       (map (fn [metric]
              {:id (:metricId metric)
               :name (:name metric)
               :unit (:unit metric)
               :stat-group-codes (remove string/blank? (map string/trim (string/split (or (:group metric) "") #",")))}))
       (map (partial metric/upsert ds))
       doall)
  (log/debug :seed/finished :metrics))

(defn seed-seasons
  [ds]
  (log/debug :seed/starting :seasons)
  (->> @(api-seasons/get-seasons-all {:query-params {:sportId 1}})
       :body
       :seasons
       (map (fn [season]
              {:id (:seasonId season)
               :spring-start-date (:springStartDate season)
               :spring-end-date (:springEndDate season)
               :regular-season-start-date (:regularSeasonStartDate season)
               :regular-season-end-date (:regularSeasonEndDate season)
               :post-season-start-date (:postSeasonStartDate season)
               :post-season-end-date (:postSeasonEndDate season)}))
       (map (partial season/upsert ds))
       doall)
  (log/debug :seed/finished :seasons))

(defn seed-venues
  [ds]
  (log/debug :seed/starting :venues)
  (->> @(api-venues/get-venues {:query-params {:season (season/get-current-id ds)}})
       :body
       :venues
       (map (partial venue/upsert ds))
       doall)
  (log/debug :seed/finished :venues))

(defn seed-sports
  [ds]
  (log/debug :seed/starting :sports)
  (->> @(api-sports/get-sports {})
       :body
       :sports
       (filter #(= (:code %) SPORT_CODE_MLB))
       (map (partial sport/upsert ds))
       doall)
  (log/debug :seed/finished :sports))

(defn seed-leagues
  [ds]
  (log/debug :seed/starting :leagues)
  (->> @(api-leagues/get-leagues {:query-params {:sportId (sport/get-mlb-id ds)
                                                 :seasons (season/get-current-id ds)}})
       :body
       :leagues
       (filter #(or (= (:orgCode %) LEAGUE_CODE_AL) (= (:orgCode %) LEAGUE_CODE_NL)))
       (map (fn [league]
              {:id (:id league)
               :code (:orgCode league)
               :name (:name league)
               :sport-id (:id (:sport league))}))
       (map (partial league/upsert ds))
       doall)
  (log/debug :seed/finished :leagues))

(defn seed-divisions
  [ds]
  (log/debug :seed/starting :divisions)
  (->> @(api-divisions/get-divisions {:query-params {:sportId (sport/get-mlb-id ds)}})
       :body
       :divisions
       (map (fn [division]
              {:id (:id division)
               :code (:abbreviation division)
               :name (:name division)
               :sport-id (:id (:sport division))
               :league-id (:id (:league division))}))
       (map (partial division/upsert ds))
       doall)
  (log/debug :seed/finished :divisions))

(defn seed-teams
  [ds]
  (log/debug :seed/starting :teams)
  (->> @(api-teams/get-teams {:query-params {:sportId (sport/get-mlb-id ds)
                                             :leagueId (league/get-mlb-league-ids ds)
                                             :hydrate "team"}})
       :body
       :teams
       (map (fn [team]
              {:id (:id team)
               :name (:name team)
               :team-code (:teamCode team)
               :abbreviation (:abbreviation team)
               :team-name (:teamName team)
               :location-name (:locationName team)
               :venue-id (:id (:venue team))
               :league-id (:id (:league team))
               :division-id (:id (:division team))}))
       (map (partial team/upsert ds))
       doall)
  (log/debug :seed/finished :teams))

(defn seed-roster-player
  [ds player-id]
  (log/debug :seed/starting {:roster-player player-id})
  (->> @(api-people/get-person {:path-params {:id player-id}})
       :body
       :people
       (map (fn [player]
              {:id (:id player)
               :first-name (:firstName player)
               :last-name (:lastName player)
               :full-name (:fullName player)
               :primary-number (:primaryNumber player)
               :birth-date (:birthDate player)
               :height (:height player)
               :weight (:weight player)
               :active (:active player)
               :bats (:code (:batSide player))
               :throws (:code (:pitchHand player))
               :strike-zone-top (:strikeZoneTop player)
               :strike-zone-bottom (:strikeZoneBottom player)
               :primary-position-code (:code (:primaryPosition player))
               :debut-season (or
                              (some-> player
                                      :mlbDebutDate
                                      (LocalDate/parse)
                                      (.getYear)
                                      (String/valueOf))
                              (season/get-current-id ds))}))
       first
       (player/upsert ds))
  (log/debug :seed/finished {:roster-player player-id}))

(defn seed-rosters
  [ds]
  (log/debug :seed/starting :rosters)
  (map
   (fn [team-id]
     (let [roster (->> @(api-teams/get-team-roster {:path-params {:id team-id}
                                                    :query-params {:rosterType "active"
                                                                   :season (season/get-current-id ds)}})
                       :body
                       :roster)]
            ; this is hacky but whatever. could at least put it in a txn
       (roster/delete-by-team-id ds team-id)
       (doall
        (map
         (fn [roster-element]
           (seed-roster-player ds (:id (:person roster-element)))
           (roster/upsert ds {:player-id (:id (:person roster-element))
                              :team-id (:parentTeamId roster-element)
                              :status (:code (:status roster-element))}))
         roster))))
   (map :id (team/get-all ds)))
  (log/debug :seed/finished :rosters))

(defn- map-batting-stats
  [stats]
  {:player-id (:id (:player stats))
   :season (:season stats)
   :games-played (:gamesPlayed (:stat stats))
   :hits (:hits (:stat stats))
   :at-bats (:atBats (:stat stats))
   :plate-appearances (:plateAppearances (:stat stats))
   :ground-outs (:groundOuts (:stat stats))
   :air-outs (:airOuts (:stat stats))
   :strike-outs (:strikeOuts (:stat stats))
   :base-on-balls (:baseOnBalls (:stat stats))
   :runs (:runs (:stat stats))
   :doubles (:doubles (:stat stats))
   :triples (:triples (:stat stats))
   :home-runs (:homeRuns (:stat stats))
   :total-bases (:totalBases (:stat stats))
   :rbi (:rbi (:stat stats))
   :left-on-base (:leftOnBase (:stat stats))
   :intentional-walks (:intentionalWalks (:stat stats))
   :hit-by-pitch (:hitByPitch (:stat stats))
   :ground-into-double-play (:groundIntoDoublePlay (:stat stats))
   :number-of-pitches (:numberOfPitches (:stat stats))
   :sac-bunts (:sacBunts (:stat stats))
   :sac-flies (:sacFlies (:stat stats))})

(defn seed-previous-seasons-batting-stats
  [ds]
  (log/debug :seed/starting :previous-seasons-batting-stats)
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (remove #(player-stats/player-batting-stats-already-stored ds % current-season))
         (mapcat (fn [player-id]
                   (->> @(api-people/get-person-stats {:path-params {:id player-id}
                                                       :query-params {:stats "yearByYear"
                                                                      :group "hitting"}})
                        :body
                        :stats
                        (filter #(= (:displayName (:group %)) "hitting"))
                        first
                        :splits
                        (remove #(= (:season %) current-season)))))
         (map map-batting-stats)
         (map (partial player-stats/upsert-batting-stats ds))
         doall))
  (log/debug :seed/finished :previous-seasons-batting-stats))

(defn seed-current-season-batting-stats
  [ds]
  (log/debug :seed/starting :current-season-batting-stats)
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (map (fn [player-id]
                (let [api-stats (->> @(api-people/get-person-stats {:path-params {:id player-id}
                                                                    :query-params {:stats "season"
                                                                                   :group "hitting"}})
                                     :body
                                     :stats
                                     (filter #(= (:displayName (:group %)) "hitting"))
                                     first
                                     :splits
                                     (filter #(= (:season %) current-season))
                                     first)]
                  (if api-stats
                    api-stats
                    {:season current-season
                     :player {:id player-id}
                     :stat {:gamesPlayed 0
                            :hits 0
                            :atBats 0
                            :plateAppearances 0
                            :groundOuts 0
                            :airOuts 0
                            :strikeOuts 0
                            :baseOnBalls 0
                            :runs 0
                            :doubles 0
                            :triples 0
                            :homeRuns 0
                            :totalBases 0
                            :rbi 0
                            :leftOnBase 0
                            :intentionalWalks 0
                            :hitByPitch 0
                            :groundIntoDoublePlay 0
                            :numberOfPitches 0
                            :sacBunts 0
                            :sacFlies 0}}))))
         (map map-batting-stats)
         (map (partial player-stats/upsert-batting-stats ds))
         doall))
  (log/debug :seed/finished :current-season-batting-stats))

(defn seed
  []
  (let [ds (db/get-datasource)
        seeds [seed-game-types
               seed-hit-trajectories
               seed-pitch-codes
               seed-pitch-types
               seed-positions
               seed-roster-types
               seed-skies
               seed-standing-types
               seed-winds
               seed-stat-groups
               seed-stat-types
               seed-metrics
               seed-seasons
               seed-venues
               seed-sports
               seed-leagues
               seed-divisions
               seed-teams
               seed-rosters
               seed-previous-seasons-batting-stats
               seed-current-season-batting-stats]]
    (log/info :seeds/starting {})
    (doall (map (fn [seed] (seed ds)) seeds))
    (log/info :seeds/finished {})))

(defn seed-daily
  []
  (let [ds (db/get-datasource)
        seeds [seed-rosters
               seed-previous-seasons-batting-stats
               seed-current-season-batting-stats]]
    (log/info :seeds-daily/starting {})
    (doall (map (fn [seed] (seed ds)) seeds))
    (log/info :seeds-daily/finished {})))
