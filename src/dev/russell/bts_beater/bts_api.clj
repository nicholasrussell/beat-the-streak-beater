(ns dev.russell.bts-beater.bts-api)

;; {"success":{"dashboard":{"currentStreak":6,"bestStreak":6,"rank":4917,"isTied":0,"accuracy":71.43,"percentile":61,"nextStreakGoal":10,"isMulliganUsed":false,"isMulliganAllowed":false,"isMulliganCanBeUsed":false,"numberPredictions":16,"numberFriendsEntries":0}},"errors":[]}
(def dashboard-url "https://www.mlb.com/apps/beat-the-streak/game/api/results/dashboard")

;; matchups
(def units-url "https://www.mlb.com/apps/beat-the-streak/game/json/units.json")

(def rounds-url "https://www.mlb.com/apps/beat-the-streak/game/json/rounds.json")

(def predictions-url "https://www.mlb.com/apps/beat-the-streak/game/api/predictions")

(def checksums-url "https://www.mlb.com/apps/beat-the-streak/game/json/checksums.json")

(def batter-url "https://www.mlb.com/apps/beat-the-streak/game/api/players/1458/batter_details/123/134")

(def suggested-players-url "https://www.mlb.com/apps/beat-the-streak/game/json/suggested_players.json")

;; To make picks
;; POST https://www.mlb.com/apps/beat-the-streak/game/api/predictions will set the predictions
;; pick one
;; {"predictions":[{"unitId":123,"playerId":1458}]}
;; pick two
;; {"predictions":[{"playerId":1300,"unitId":115},{"playerId":1458,"unitId":123}]}

