(ns dev.russell.bts-picker.db.seed
  (:require [clojure.string :as string]
            [taoensso.timbre :as log]
            [dev.russell.batboy.divisions.core :as api-divisions]
            [dev.russell.batboy.leagues.core :as api-leagues]
            [dev.russell.batboy.meta.core :as api-meta]
            [dev.russell.batboy.people.core :as api-people]
            [dev.russell.batboy.schedules.core :as api-schedules]
            [dev.russell.batboy.seasons.core :as api-seasons]
            [dev.russell.batboy.sports.core :as api-sports]
            [dev.russell.batboy.teams.core :as api-teams]
            [dev.russell.batboy.venues.core :as api-venues]
            [dev.russell.bts-picker.constants :refer [SPORT_CODE_MLB LEAGUE_CODE_AL LEAGUE_CODE_NL]]
            [dev.russell.bts-picker.db.core :as db]
            [dev.russell.bts-picker.db.models.division :as division]
            [dev.russell.bts-picker.db.models.game-type :as game-type]
            [dev.russell.bts-picker.db.models.game :as game]
            [dev.russell.bts-picker.db.models.hit-trajectory :as hit-trajectory]
            [dev.russell.bts-picker.db.models.league :as league]
            [dev.russell.bts-picker.db.models.metric :as metric]
            [dev.russell.bts-picker.db.models.pitch-code :as pitch-code]
            [dev.russell.bts-picker.db.models.pitch-type :as pitch-type]
            [dev.russell.bts-picker.db.models.player :as player]
            [dev.russell.bts-picker.db.models.player-stats :as player-stats]
            [dev.russell.bts-picker.db.models.position :as position]
            [dev.russell.bts-picker.db.models.probable-pitcher :as probable-pitcher]
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
            [dev.russell.bts-picker.db.models.wind :as wind]
            [dev.russell.bts-picker.util.date.core :as date])
  (:import [java.time LocalDate]))

(defn seed-game-types
  [ds]
  (log/debug :seed/starting :game-types)
  (->> @(api-meta/get-game-types {})
       :body
       (game-type/upsert-batch ds))
  (log/debug :seed/finished :game-types))

(defn seed-hit-trajectories
  [ds]
  (log/debug :seed/starting :hit-trajectories)
  (->> @(api-meta/get-hit-trajectories {})
       :body
       (hit-trajectory/upsert-batch ds))
  (log/debug :seed/finished :hit-trajectories))

(defn seed-pitch-codes
  [ds]
  (log/debug :seed/starting :pitch-codes)
  (->> @(api-meta/get-pitch-codes {})
       :body
       (pitch-code/upsert-batch ds))
  (log/debug :seed/finished :pitch-codes))

(defn seed-pitch-types
  [ds]
  (log/debug :seed/starting :pitch-types)
  (->> @(api-meta/get-pitch-types {})
       :body
       (pitch-type/upsert-batch ds))
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
       (position/upsert-batch ds))
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
       (roster-type/upsert-batch ds))
  (log/debug :seed/finished :roster-types))

(defn seed-skies
  [ds]
  (log/debug :seed/starting :skies)
  (->> @(api-meta/get-sky {})
       :body
       (sky/upsert-batch ds))
  (log/debug :seed/finished :skies))

(defn seed-standing-types
  [ds]
  (log/debug :seed/starting :standing-types)
  (->> @(api-meta/get-standings-types {})
       :body
       (standing-type/upsert-batch ds))
  (log/debug :seed/finished :standing-types))

(defn seed-winds
  [ds]
  (log/debug :seed/starting :winds)
  (->> @(api-meta/get-wind-direction {})
       :body
       (wind/upsert-batch ds))
  (log/debug :seed/finished :winds))

(defn seed-stat-groups
  [ds]
  (log/debug :seed/starting :stat-groups)
  (->> @(api-meta/get-stat-groups {})
       :body
       (map (fn [stat-group]
              {:code (:displayName stat-group)}))
       (stat-group/upsert-batch ds))
  (log/debug :seed/finished :stat-groups))

(defn seed-stat-types
  [ds]
  (log/debug :seed/starting :stat-types)
  (->> @(api-meta/get-stat-types {})
       :body
       (map (fn [stat-type]
              {:code (:displayName stat-type)}))
       (stat-type/upsert-batch ds))
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
       (metric/upsert-batch ds))
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
       (season/upsert-batch ds))
  (log/debug :seed/finished :seasons))

(defn seed-venues
  [ds]
  (log/debug :seed/starting :venues)
  (->> @(api-venues/get-venues {:query-params {:season (season/get-current-id ds)}})
       :body
       :venues
       (venue/upsert-batch ds))
  (log/debug :seed/finished :venues))

(defn seed-sports
  [ds]
  (log/debug :seed/starting :sports)
  (->> @(api-sports/get-sports {})
       :body
       :sports
       (filter #(= (:code %) SPORT_CODE_MLB))
       (sport/upsert-batch ds))
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
       (league/upsert-batch ds))
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
       (division/upsert-batch ds))
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
       (team/upsert-batch ds))
  (log/debug :seed/finished :teams))

(defn seed-roster-player
  [ds player-id]
  (->> @(api-people/get-person {:path-params {:id player-id}})
       :body
       :people
       (map (fn [player]
              {:id (:id player)
               :first-name (:firstName player)
               :last-name (:lastName player)
               :full-name (:fullName player)
               :primary-number (let [number (or (:primaryNumber player) "0")]
                                 (if (= number "null")
                                   "0"
                                   number))
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
       (player/upsert ds)))

(defn seed-rosters
  [ds]
  (log/debug :seed/starting :rosters)
  (->> (team/get-all ds)
       (pmap :id)
       (pmap
        (fn [team-id]
          (log/debug :seed/starting {:roster team-id})
          (let [roster (->> @(api-teams/get-team-roster {:path-params {:id team-id}
                                                         :query-params {:rosterType "active"
                                                                        :season (season/get-current-id ds)}})
                            :body
                            :roster)]
                                        ; this is hacky but whatever. could at least put it in a txn
                                        ; this is so we remove guys who are no longer on the team roster
            (roster/delete-by-team-id ds team-id)
            (doall
             (pmap
              (fn [roster-element]
                (seed-roster-player ds (:id (:person roster-element)))
                (roster/upsert ds {:player-id (:id (:person roster-element))
                                   :team-id (:parentTeamId roster-element)
                                   :status (:code (:status roster-element))}))
              roster))
            (log/debug :seed/finished {:roster team-id}))))
       doall)
  (log/debug :seed/finished :rosters))

(defn get-player-with-stats
  [player-id]
  (comment "https://statsapi.mlb.com/api/v1/people/677951?hydrate=currentTeam,team,stats(type=[yearByYear,yearByYearAdvanced,careerRegularSeason,careerAdvanced,availableStats](team(league)),leagueListId=mlb_hist)&site=en"))

(defn- map-batting-stats
  [stats]
  (let [stat (:stat stats)]
    {:player-id (:id (:player stats))
     :season (:season stats)
     :air-outs (:airOuts stat)
     :at-bats (:atBats stat)
     :base-on-balls (:baseOnBalls stat)
     :doubles (:doubles stat)
     :games-played (:gamesPlayed stat)
     :ground-into-double-play (:groundIntoDoublePlay stat)
     :ground-outs (:groundOuts stat)
     :hit-by-pitch (:hitByPitch stat)
     :hits (:hits stat)
     :home-runs (:homeRuns stat)
     :intentional-walks (:intentionalWalks stat)
     :left-on-base (:leftOnBase stat)
     :number-of-pitches (:numberOfPitches stat)
     :plate-appearances (:plateAppearances stat)
     :rbi (:rbi stat)
     :runs (:runs stat)
     :sac-bunts (:sacBunts stat)
     :sac-flies (:sacFlies stat)
     :stolen-bases (:stolenBases stat)
     :strike-outs (:strikeOuts stat)
     :total-bases (:totalBases stat)
     :triples (:triples stat)}))

(defn- map-pitching-stats
  [stats]
  (let [stat (:stat stats)
        innings-pitched (if-let [ip (string/trim (or (:inningsPitched stat) ""))]
                          (let [splits (string/split ip #"\.")
                                complete (Integer/parseInt (string/replace (first splits) #"," ""))
                                partial (if (> (count splits) 1)
                                          (Integer/parseInt (second splits))
                                          0)]
                            {:complete complete
                             :partial partial})
                          {:complete 0
                           :partial 0})]
    {:player-id (:id (:player stats))
     :season (:season stats)
     :air-outs (:airOuts stat)
     :at-bats (:atBats stat)
     :balks (:balks stat)
     :base-on-balls (:baseOnBalls stat)
     :batters-faced (:battersFaced stat)
     :blown-saves (:blownSaves stat)
     :catchers-interference (:catchersInterference stat)
     :caught-stealing (:caughtStealing stat)
     :complete-games (:completeGames stat)
     :doubles (:doubles stat)
     :earned-runs (:earnedRuns stat)
     :games-finished (:gamesFinished stat)
     :games-pitched (:gamesPitched stat)
     :games-played (:gamesPlayed stat)
     :games-started (:gamesStarted stat)
     :ground-into-double-play (:groundIntoDoublePlay stat)
     :ground-outs (:groundOuts stat)
     :hit-batsmen (:hitBatsmen stat)
     :hit-by-pitch (:hitByPitch stat)
     :hits (:hits stat)
     :holds (:holds stat)
     :home-runs (:homeRuns stat)
     :inherited-runners (:inheritedRunners stat)
     :inherited-runners-scored (:inheritedRunnersScored stat)
     :innings-pitched (:inningsPitched stat)
     :innings-pitched-complete (:complete innings-pitched)
     :innings-pitched-partial (:partial innings-pitched)
     :intentional-walks (:intentionalWalks stat)
     :losses (:losses stat)
     :number-of-pitches (:numberOfPitches stat)
     :outs (:outs stat)
     :pickoffs (:pickoffs stat)
     :runs (:runs stat)
     :sac-bunts (:sacBunts stat)
     :sac-flies (:sacFlies stat)
     :save-opportunities (:saveOpportunities stat)
     :saves (:saves stat)
     :shutouts (:shutouts stat)
     :stolen-bases (:stolenBases stat)
     :strike-outs (:strikeOuts stat)
     :strikes (:strikes stat)
     :total-bases (:totalBases stat)
     :triples (:triples stat)
     :wild-pitches (:wildPitches stat)
     :wins (:wins stat)}))

(defn seed-previous-seasons-batting-stats
  [ds]
  (log/debug :seed/starting :previous-seasons-batting-stats)
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (remove #(player-stats/player-batting-stats-already-stored ds % current-season))
         (pmap (fn [player-id]
                 (->> @(api-people/get-person-stats {:path-params {:id player-id}
                                                     :query-params {:stats "yearByYear"
                                                                    :group "hitting"}})
                      :body
                      :stats
                      (filter #(= (:displayName (:group %)) "hitting"))
                      first
                      :splits
                      (remove #(= (:season %) current-season)))))
         (mapcat identity)
         (pmap map-batting-stats)
         (player-stats/upsert-batting-stats-batch ds)))
  (log/debug :seed/finished :previous-seasons-batting-stats))

(defn seed-current-season-batting-stats
  [ds]
  (log/debug :seed/starting :current-season-batting-stats)
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (pmap (fn [player-id]
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
                             :stolenBases 0
                             :leftOnBase 0
                             :intentionalWalks 0
                             :hitByPitch 0
                             :groundIntoDoublePlay 0
                             :numberOfPitches 0
                             :sacBunts 0
                             :sacFlies 0}}))))
         (pmap map-batting-stats)
         (player-stats/upsert-batting-stats-batch ds)))
  (log/debug :seed/finished :current-season-batting-stats))

(defn current-batting-stat-splits
  [ds]
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (pmap (fn [player-id]
                 (let [api-stats (->> @(api-people/get-person-stats {:path-params {:id player-id}
                                                                     :query-params {:stats "statSplits"
                                                                                    :group "hitting"
                                                                                    :season current-season
                                                                                    :gameType "R"
                                                                                    :sitCodes "h,a,d,n,g,t,3,4,5,6,7,8,9,10,preas,posas,vl,vr,r0,r1,r2,r3,r12,r13,r23,r123,risp,o0,o1,o2,i01,i02,i03,i04,i05,i06,i07,i08,i09,ix,b1,b2,b3,b4,b5,b6,b7,b8,b9,lo,lc,ac,bc,sp,rp,h1,h2"}})
                                      :body)]
                   api-stats))))))

(defn hitting-vs-team
  [player-id]
  (comment "https://statsapi.mlb.com/api/v1/people/677951/stats?stats=vsTeam&group=hitting&opposingTeamId=142&season=2024&language=en"))

(defn seed-previous-seasons-pitching-stats
  [ds]
  (log/debug :seed/starting :previous-seasons-pitching-stats)
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (remove #(player-stats/player-pitching-stats-already-stored ds % current-season))
         (pmap (fn [player-id]
                 (->> @(api-people/get-person-stats {:path-params {:id player-id}
                                                     :query-params {:stats "yearByYear"
                                                                    :group "pitching"}})
                      :body
                      :stats
                      (filter #(= (:displayName (:group %)) "pitching"))
                      first
                      :splits
                      (remove #(= (:season %) current-season)))))
         (mapcat identity)
         (pmap map-pitching-stats)
         (player-stats/upsert-pitching-stats-batch ds)))
  (log/debug :seed/finished :previous-seasons-pitching-stats))

(defn seed-current-season-pitching-stats
  [ds]
  (log/debug :seed/starting :current-season-pitching-stats)
  (let [current-season (season/get-current-id ds)]
    (->> (player/get-active-player-ids ds)
         (pmap (fn [player-id]
                 (let [api-stats (->> @(api-people/get-person-stats {:path-params {:id player-id}
                                                                     :query-params {:stats "season"
                                                                                    :group "pitching"}})
                                      :body
                                      :stats
                                      (filter #(= (:displayName (:group %)) "pitching"))
                                      first
                                      :splits
                                      (filter #(= (:season %) current-season))
                                      first)]
                   (if api-stats
                     api-stats
                     {:season current-season
                      :player {:id player-id}
                      :stat {:gamesPlayed 0
                             :inningsPitched "0"
                             :hits 0
                             :atBats 0
                             :battersFaced 0
                             :groundOuts 0
                             :airOuts 0
                             :strikeOuts 0
                             :baseOnBalls 0
                             :intentionalWalks 0
                             :hitBatsmen 0
                             :hitByPitch 0
                             :runs 0
                             :doubles 0
                             :triples 0
                             :homeRuns 0
                             :totalBases 0
                             :earnedRuns 0
                             :inheritedRunners 0
                             :inheritedRunnersScored 0
                             :numberOfPitches 0
                             :groundIntoDoublePlay 0
                             :balks 0
                             :sacBunts 0
                             :sacFlies 0
                             :wildPitches 0
                             :pickoffs 0
                             :stolenBases 0
                             :caughtStealing 0
                             :catchersInterference 0
                             :holds 0
                             :saveOpportunities 0
                             :blownSaves 0
                             :saves 0
                             :gamesPitched 0
                             :gamesStarted 0
                             :completeGames 0
                             :gamesFinished 0
                             :shutouts 0
                             :wins 0
                             :losses 0}}))))
         (pmap map-pitching-stats)
         (player-stats/upsert-pitching-stats-batch ds)))
  (log/debug :seed/finished :current-season-pitching-stats))

(defn seed-games
  [ds date]
  (log/debug :seed/starting :games)
  (let [schedule (->> @(api-schedules/get-schedules {:multi-param-style :comma-separated
                                                     :query-params {:sportId 1
                                                                    :date date
                                                                    :leagueId [103 104]
                                                                    :hydrate ["team" "probablePitcher(note)"]
                                                                    :language "en"}})
                      :body
                      :dates
                      (filter #(= date (:date %)))
                      first)]
    (game/upsert-games ds schedule)
    (log/debug :seed/finished :games)
    schedule))

(defn seed-probable-pitchers
  [ds schedule]
  (log/debug :seed/starting :probable-pitchers)
  (probable-pitcher/upsert-probable-pitchers ds schedule)
  (log/debug :seed/finished :probable-pitchers))

(defn- seed-batter-vs-pitcher
  [ds matchup]
  (let [stat (->> @(api-people/get-person-stats {:path-params {:id (:batter-id matchup)}
                                                 :query-params {:stats "vsPlayerTotal"
                                                                :group "hitting"
                                                                :opposingPlayerId (:pitcher-id matchup)}})
                  :body
                  :stats
                  first
                  :splits
                  first
                  :stat)
        mapped {:batter-id (:batter-id matchup)
                :pitcher-id (:pitcher-id matchup)
                :air-outs (or (:airOuts stat) 0)
                :at-bats (or (:atBats stat) 0)
                :base-on-balls (or (:baseOnBalls stat) 0)
                :catchers-interference (or (:catchersInterference stat) 0)
                :doubles (or (:doubles stat) 0)
                :games-played (or (:gamesPlayed stat) 0)
                :ground-into-double-play (or (:groundIntoDoublePlay stat) 0)
                :ground-into-triple-play (or (:groundIntoTriplePlay stat) 0)
                :ground-outs (or (:groundOuts stat) 0)
                :hit-by-pitch (or (:hitByPitch stat) 0)
                :hits (or (:hits stat) 0)
                :home-runs (or (:homeRuns stat) 0)
                :intentional-walks (or (:intentionalWalks stat) 0)
                :left-on-base (or (:leftOnBase stat) 0)
                :number-of-pitches (or (:numberOfPitches stat) 0)
                :plate-appearances (or (:plateAppearances stat) 0)
                :rbi (or (:rbi stat) 0)
                :sac-bunts (or (:sacBunts stat) 0)
                :sac-flies (or (:sacFlies stat) 0)
                :strike-outs (or (:strikeOuts stat) 0)
                :total-bases (or (:totalBases stat) 0)
                :triples (or (:triples stat) 0)}]
    (player-stats/upsert-batter-vs-pitcher-stats ds mapped)))

(defn seed-batters-vs-pitchers
  [ds matchups]
  (log/debug :seed/starting :batter-vs-pitcher)
  (->> matchups
       (reduce-kv
        (fn [acc _ matchup]
          (let [away-pitcher (-> matchup :away :probable-pitcher-id)
                home-pitcher (-> matchup :home :probable-pitcher-id)
                seed-matchup (fn [pitcher-id batter-ids]
                               (if pitcher-id
                                 (map (fn [batter-id]
                                        {:batter-id batter-id
                                         :pitcher-id pitcher-id})
                                      batter-ids)
                                 []))]
            (concat
             acc
             (concat
              (seed-matchup home-pitcher (-> matchup :away :roster-ids))
              (seed-matchup away-pitcher (-> matchup :home :roster-ids))))))
        [])
       (pmap (fn [bvp] (seed-batter-vs-pitcher ds bvp)))
       doall)
  (log/debug :seed/finished :batter-vs-pitcher))

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
               seed-teams]]
    (log/info :seeds/starting {})
    (doall (map (fn [seed] (seed ds)) seeds))
    (log/info :seeds/finished {})))

(defn seed-daily
  [date]
  (let [ds (db/get-datasource)
        seeds [seed-rosters
               seed-previous-seasons-batting-stats
               seed-current-season-batting-stats
               seed-previous-seasons-pitching-stats
               seed-current-season-pitching-stats]]
    (log/info :seeds-daily/starting {})
    (doall (map (fn [seed] (seed ds)) seeds))
    (let [schedule (seed-games ds date)]
      (seed-probable-pitchers ds schedule)
      (let [matchups (game/get-matchups-by-date ds date)]
        (seed-batters-vs-pitchers ds matchups)))
    (log/info :seeds-daily/finished {})))

