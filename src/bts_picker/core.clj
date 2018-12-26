(ns bts-picker.core
  (:require [clojure.tools.trace :refer :all]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [bts-picker.util :as util]
            [bts-picker.games]
            [bts-picker.probable-pitchers]
            [bts-picker.batter-vs-pitcher]
            [bts-picker.season-batters]
            [bts-picker.daily-batters]
            [bts-picker.weather]))

(defn- pitchers-for-games
  [games probable-pitchers]
  (mapv
   (fn [game]
     (let [pitchers (filterv #(= (:game-id %) (:id game)) probable-pitchers)]
       (assoc game :probable-pitchers pitchers)))
   games))

(defn- safe-stat
  [player stat]
  (if (and (contains? player stat) (not (str/blank? (stat player))))
    (read-string (stat player))
    0))

(defn- safe-ratio
  [num denom]
  (if (> denom 0)
    (double (/ num denom))
    0.0))

(defn- safe-hits
  [player]
  (safe-stat player :h))

(defn- safe-ab
  [player]
 (safe-stat player :ab))

(defn- safe-s-ip
  [player]
  (safe-stat player :s_ip))

(defn- safe-s-hits
  [player]
  (safe-stat player :s_h))

(defn- safe-s-ab
  [player]
  (safe-stat player :s_ab))

(defn- calculate-sh-sip-ratio
  [pitcher]
  (let [h (safe-s-hits pitcher)
        ip (safe-s-ip pitcher)]
    {:s-h h
     :s-ip ip
     :ratio (safe-ratio h ip)}))

(defn- pitcher-heuristic
  [pitcher]
  (let [sh-sip-ratio (calculate-sh-sip-ratio pitcher)]
    (:ratio sh-sip-ratio)))

(defn- rank-pitchers
  [pitchers]
  (sort #(compare (pitcher-heuristic %2) (pitcher-heuristic %1)) pitchers))

(defn- calculate-bvp-h-ab-ratio
  [batter]
  (let [h (safe-hits batter)
        ab (safe-ab batter)]
    {:bvp-h h
     :bvp-ab ab
     :ratio (safe-ratio h ab)}))

(defn- calculate-season-h-ab-ratio
  [batter]
  (let [h (safe-s-hits batter)
        ab (safe-s-ab batter)]
    {:s-h h
     :s-ab ab
     :ratio (safe-ratio h ab)}))

(defn- calculate-last-7-h-ab-ratio
  [batter]
  (let [h (:h batter)
        ab (:ab batter)]
    {:last-7-h h
     :last-7-ab ab
     :ratio (safe-ratio h ab)}))

(defn- batter-heuristic
  [batter]
  (let [bvp-h-ab-ratio (calculate-bvp-h-ab-ratio (:bvp batter))
        sh-sab-ratio (calculate-season-h-ab-ratio batter)
        last-7-h-ab-ratio (calculate-last-7-h-ab-ratio (:last-7 batter))]
    {:bvp bvp-h-ab-ratio
     :season sh-sab-ratio
     :last-7 last-7-h-ab-ratio}))

(defn- batter-compare
  [b1 b2]
  (let [b1-data (batter-heuristic b1)
        b2-data (batter-heuristic b2)
        bvp-compare (compare (:ratio (:bvp b2-data)) (:ratio (:bvp b1-data)))]
    (if (= bvp-compare 0)
      (let [last-7-compare (compare (:ratio (:last-7 b2-data)) (:ratio (:last-7 b1-data)))]
        (if (= last-7-compare 0)
          (compare (:ratio (:season b2-data)) (:ratio (:season b1-data)))
          last-7-compare))
      bvp-compare)))

(defn- rank-batters
  [batters]
  (sort batter-compare batters))

;; TODO combine these
(defn- opposing-team-id-for-id
  [games team-id]
  (reduce
   (fn [acc cur]
     (condp = team-id
       (:home-team-id cur) (reduced (:away-team-id cur))
       (:away-team-id cur) (reduced (:home-team-id cur))
       acc))
   team-id
   games))

(defn- team-name-for-id
  [games team-id]
  (let [name-or-id (fn [name id] (if (str/blank? name) id name))]
    (reduce
     (fn [acc cur]
       (condp = team-id
         (:home-team-id cur) (reduced (name-or-id (:home-team-name cur) team-id))
         (:away-team-id cur) (reduced (name-or-id (:away-team-name cur) team-id))
         acc))
     team-id
     games)))

(defn- opposing-team-name-for-id
  [games team-id]
  (let [name-or-id (fn [name id] (if (str/blank? name) id name))]
    (reduce
     (fn [acc cur]
       (condp = team-id
         (:home-team-id cur) (reduced (name-or-id (:away-team-name cur) team-id))
         (:away-team-id cur) (reduced (name-or-id (:home-team-name cur) team-id))
         acc))
     team-id
     games)))

(defn- venue-location-for-team-id
  [games team-id]
  (reduce
   (fn [acc cur]
     (if (or (= team-id (:home-team-id cur))
             (= team-id (:away-team-id cur)))
       (if (str/blank? (:venue-location cur))
         (reduced "")
         (reduced (:venue-location cur)))
       acc))
   team-id
   games))

(defn- pitcher-name-for-id
  [pitchers pitcher-id]
  (reduce
   (fn [acc cur]
     (if (= pitcher-id (:pitcher-id cur))
       (reduced (:name cur))
       acc))
   pitcher-id
   pitchers))

(defn- bvp-data
  [date games pitcher]
  (let [bvp (bts-picker.batter-vs-pitcher/batter-vs-pitcher date (opposing-team-id-for-id games (:team-id pitcher)) (:pitcher-id pitcher))]
    {:pitcher pitcher
     :batters (:batters bvp)}))

(defn- all-batter-stats-from-bvp
  [date bvp]
  (let [all-bvp-batters (reduce (fn [acc cur] (concat acc (map #(assoc % :pitcher-id (:pitcher-id (:pitcher cur))) (:batters cur)))) [] bvp)]
    (pmap #(assoc (bts-picker.season-batters/batter-season date (:player_id %)) :bvp %) all-bvp-batters)))

(defn- last-n-days-batter-data
  [date batter n]
  (pmap
   (fn [n-day-ago] (bts-picker.daily-batters/daily-batters (util/n-days-ago date n-day-ago) (:id batter)))
   (range 1 (+ n 1))))

(defn- all-batter-stats
  [date batters]
  (pmap
   (fn [batter]
     (let [last-7-days (reduce (fn [acc cur]
                                 {:h (+ (:h acc) (:h cur))
                                  :ab (+ (:ab acc) (:ab cur))
                                  :bb (+ (:bb acc) (:bb cur))})
                               {:h 0 :ab 0 :bb 0}
                               (last-n-days-batter-data date batter 7))]
       (assoc batter
              :last-7
              last-7-days)))
   batters))

(defn- game-weather-data
  [games]
  (pmap
   (fn [game]
     (let [loc (:venue-location game)]
       (assoc (bts-picker.weather/weather-for-location (:venue-location game))
              :location
              loc)))
   games))

(defn- print-worst-pitcher-data
  [games worst-pitchers]
  (println "Worst Pitchers")
  (let [idx-pitcher (map-indexed vector worst-pitchers)
        table-data (map
                    (fn [[idx pitcher]]
                      (let [rank (+ idx 1)
                            h (safe-s-hits pitcher)
                            ip (safe-s-ip pitcher)
                            ratio (format "%.2f" (:ratio (calculate-sh-sip-ratio pitcher)))]
                        {:rank rank
                         :name (:name pitcher)
                         :id (:pitcher-id pitcher)
                         :team (team-name-for-id games (:team-id pitcher))
                         :opposing-team (opposing-team-name-for-id games (:team-id pitcher))
                         :location (venue-location-for-team-id games (:team-id pitcher))
                         :s_hits h
                         :s_ip ip
                         :ratio ratio}))
                    idx-pitcher)]
    (pprint/print-table [:rank :name :id :team :opposing-team :location :s_hits :s_ip :ratio] table-data)))

(defn- print-best-batter-data
  [games pitchers best-batters]
  (println "Best Batters")
  (let [idx-batter (map-indexed vector best-batters)
        table-data (map
                    (fn [[idx batter]]
                      (let [rank (+ idx 1)
                            bvp-data (calculate-bvp-h-ab-ratio (:bvp batter))
                            bvp-h (:bvp-h bvp-data)
                            bvp-ab (:bvp-ab bvp-data)
                            bvp-h-ab-ratio (format "%.3f" (:ratio bvp-data))
                            l7-data (calculate-last-7-h-ab-ratio (:last-7 batter))
                            l7-h (:last-7-h l7-data)
                            l7-ab (:last-7-ab l7-data)
                            l7-ratio (format "%.3f" (:ratio l7-data))
                            season-data (calculate-season-h-ab-ratio batter)
                            s-h (:s-h season-data)
                            s-ab (:s-ab season-data)
                            s-h-ab-ratio (format "%.3f" (:ratio season-data))]
                        {:rank rank
                         :name (:player_first_last (:bvp batter))
                         ;:id (:player_id (:bvp batter))
                         :opposing-pitcher (pitcher-name-for-id pitchers (:pitcher-id (:bvp batter)))
                         :team (team-name-for-id games (:team_id (:bvp batter)))
                         :bvp-hits bvp-h
                         :bvp-ab bvp-ab
                         :bvp-ratio bvp-h-ab-ratio
                         :l7-hits l7-h
                         :l7-ab l7-ab
                         :l7-ratio l7-ratio
                         :s-hits s-h
                         :s-ab s-ab
                         :s-ratio s-h-ab-ratio}))
                    idx-batter)]
    (pprint/print-table [:rank :name :opposing-pitcher :team :bvp-hits :bvp-ab :bvp-ratio :l7-hits :l7-ab :l7-ratio :s-hits :s-ab :s-ratio] table-data)))

(defn- print-weather-data
  [weather-data]
  (println "Weather")
  (pprint/print-table [:location :description :current-temp :low-temp :high-temp :wind] weather-data))

(defn -main
  [& args]
  (let [date (util/now)
        games (bts-picker.games/games date)
        probable-pitchers (bts-picker.probable-pitchers/probable-pitchers date)
        games-with-pitchers (pitchers-for-games games probable-pitchers)
        worst-pitchers (rank-pitchers probable-pitchers)
        bvp (pmap (partial bvp-data date games) worst-pitchers)
        batters (all-batter-stats date (all-batter-stats-from-bvp date bvp))
        best-batters (rank-batters batters)
        weather (game-weather-data games)]
    (print-weather-data weather)
    (println)
    (print-worst-pitcher-data games worst-pitchers)
    (println)
    (print-best-batter-data games worst-pitchers best-batters)))

