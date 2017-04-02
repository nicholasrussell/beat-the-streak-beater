(ns bts-picker.core
  (:require [clojure.tools.trace :refer :all]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [bts-picker.util :as util]
            [bts-picker.games]
            [bts-picker.probable-pitchers]
            [bts-picker.batter-vs-pitcher]
            [bts-picker.weather]))

(defn- pitchers-for-games
  [games probable-pitchers]
  (mapv
   (fn [game]
     (let [pitchers (filterv #(= (:game-id %) (:id game)) probable-pitchers)]
       (assoc game :probable-pitchers pitchers)))
   games))

(defn- safe-hits
  [pitcher]
  (if (and (contains? pitcher :s_h) (not (str/blank? (:s_h pitcher))))
    (read-string (:s_h pitcher))
    0))

(defn- safe-ip
  [pitcher]
  (if (and (contains? pitcher :s_ip) (not (str/blank? (:s_ip pitcher))))
    (read-string (:s_ip pitcher))
    0))

(defn- pitcher-heuristic
  [p1 p2]
  (let [p1h (safe-hits p1)
        p1ip (safe-ip p1)
        p2h (safe-hits p2)
        p2ip (safe-ip p2)
        p1r (if (> p1ip 0) (/ p1h p1ip) 0)
        p2r (if (> p2ip 0) (/ p2h p2ip) 0)]
    (compare p2r p1r)))

(defn- rank-pitchers
  [pitchers]
  (sort pitcher-heuristic pitchers))

(defn- safe-b-hits
  [batter]
  (if (and (contains? batter :h) (not (str/blank? (:h batter))))
    (read-string (:h batter))
    0))

(defn- safe-ab
  [batter]
  (if (and (contains? batter :ab) (not (str/blank? (:ab batter))))
    (read-string (:ab batter))
    0))

(defn- bvp-heuristic
  [b1 b2]
  (let [b1h (safe-b-hits b1)
        b1ab (safe-ab b1)
        b2h (safe-b-hits b2)
        b2ab (safe-ab b2)
        b1avg (if (> b1ab 0) (/ b1h b1ab) 0)
        b2avg (if (> b2ab 0) (/ b2h b2ab) 0)]
    (compare b2avg b1avg)))

(defn- rank-bvp
  [batters]
  (sort bvp-heuristic batters))

(defn- bvp-data
  [date pitcher]
  (let [bvp (bts-picker.batter-vs-pitcher/batter-vs-pitcher date (:team-id pitcher) (:pitcher-id pitcher))]
    {:pitcher pitcher
     :batters (rank-bvp (:batters bvp))}))

(defn- rank-batters
  [games pitchers bvp]
  (let [all-batters (reduce (fn [acc cur] (concat acc (map #(assoc % :pitcher-id (:pitcher-id (:pitcher cur))) (:batters cur)))) [] bvp)]
    (into [] (rank-bvp all-batters))))

;; TODO combine team-name-for-id and opposing-team-name-for-id
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

(defn- game-weather-data
  [games]
  (map
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
                            h (safe-hits pitcher)
                            ip (safe-ip pitcher)
                            ratio (format "%.2f" (if (> ip 0) (double (/ h ip)) 0.0))]
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
                            h (safe-b-hits batter)
                            ab (safe-ab batter)
                            ratio (format "%.3f" (if (> ab 0) (double (/ h ab)) 0.0))]
                        {:rank rank
                         :name (:player_first_last batter)
                         :id (:player_id batter)
                         :opposing-pitcher (pitcher-name-for-id pitchers (:pitcher-id batter))
                         :team (team-name-for-id games (:team_id batter))
                         :opposing-team (opposing-team-name-for-id games (:team_id batter))
                         :location (venue-location-for-team-id games (:team_id batter))
                         :bvp_hits h
                         :bvp_ab ab
                         :ratio ratio}))
                    idx-batter)]
    (pprint/print-table [:rank :name :id :opposing-pitcher :team :opposing-team :location :bvp_hits :bvp_ab :ratio] table-data)))

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
        bvp (map (partial bvp-data date) worst-pitchers)
        best-batters (rank-batters games worst-pitchers bvp)
        weather (game-weather-data games)]
    (print-weather-data weather)
    (println)
    (print-worst-pitcher-data games worst-pitchers)
    (println)
    (print-best-batter-data games worst-pitchers best-batters)))
    
