(ns dev.russell.bts-picker.rankings.batter-vs-pitcher
  (:require [dev.russell.batboy.people.core :as people]))

(defn- batter-vs-pitcher-stats
  [batter-id pitcher-id]
  (future
    {:player-id batter-id
     :raw-stats (->> @(people/get-person-stats {:path-params {:id batter-id}
                                                :query-params {:stats "vsPlayerTotal"
                                                               :group "hitting"
                                                               :opposingPlayerId pitcher-id}})
                    :body
                    :stats)}))

(defn- raw-scores
  [raw-stats]
  (let [total-stats (->> raw-stats
                         (filter #(= (:displayName (:type %)) "vsPlayerTotal"))
                         first
                         :splits
                         first
                         :stat)]
    {:total-hit-percentage (if (or (nil? (:plateAppearances total-stats)) (<= (:plateAppearances total-stats) 0))
                             0.0
                             (double (/ (:hits total-stats) (:plateAppearances total-stats))))}))

(defn- weight
  [key score]
  (condp = key
    :total-hit-percentage (* score (/ 1.0 1.0))
    nil))

(defn- apply-weights
  [scores]
  (reduce-kv
   (fn [m k v]
     (let [w (weight k v)]
       (if w
         (assoc m k w)
         m)))
   {}
   scores))

(defn- sum-score
  [scores]
  (reduce + 0 scores))

(defn- player-score
  [player-id raw-stats]
  (let [raw-scores (raw-scores raw-stats)
        weighted-scores (apply-weights raw-scores)]
    {:player-id player-id
     :score-metadata {:raw-scores raw-scores
                      :weighted-scores weighted-scores}
     :score (sum-score (vals weighted-scores))}))

(defn- score-batter
  [player-stats]
  (future
    (player-score (:player-id @player-stats)
                  (:raw-stats @player-stats))))

(defn score-batters-vs-pitchers
  [matchups]
  (future
    (->>
     @matchups
     (mapcat (fn [matchup]
               (let [away-team-roster (-> matchup :teams :away :roster)
                     home-team-roster (-> matchup :teams :home :roster)
                     away-team-pitcher-id (-> matchup :teams :away :pitcher :player-id)
                     home-team-pitcher-id (-> matchup :teams :home :pitcher :player-id)]
                 (mapcat
                  identity
                  [(when home-team-pitcher-id
                     (map #(batter-vs-pitcher-stats (:player-id %) home-team-pitcher-id) away-team-roster))
                   (when away-team-pitcher-id
                     (map #(batter-vs-pitcher-stats (:player-id %) away-team-pitcher-id) home-team-roster))]))))
     (map score-batter))))
