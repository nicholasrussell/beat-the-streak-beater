(ns dev.russell.bts-beater.rankings.batter-vs-pitcher
  (:require [dev.russell.bts-beater.db.core :as db]
            [dev.russell.bts-beater.db.models.player-stats :as player-stats]))

(defn- weight
  [stat value]
  (condp = stat
    :hits-percentage value
    nil))

(defn- apply-weights
  [stats]
  (reduce-kv
   (fn [m k v]
     (let [w (weight k v)]
       (if w
         (assoc m k w)
         m)))
   {}
   stats))

(defn- sum-score
  [scores]
  (reduce + 0 scores))

(defn- score-batter-vs-pitcher
  [stats]
  (let [weighted-scores (apply-weights stats)]
    {:player-id (:batter-id stats)
     :pitcher-id (:pitcher-id stats)
     :score-metadata {:raw-scores stats
                      :weighted-scores weighted-scores}
     :score (sum-score (vals weighted-scores))}))

(defn score-batters-vs-pitchers
  [matchups]
  (let [ds (db/get-datasource)]
    (->> matchups
         (mapcat (fn [matchup]
                   (let [away-team-roster (-> matchup :away :roster-ids)
                         home-team-roster (-> matchup :home :roster-ids)
                         away-team-pitcher-id (-> matchup :away :probable-pitcher-id)
                         home-team-pitcher-id (-> matchup :home :probable-pitcher-id)]
                     (mapcat
                      identity
                      [(when home-team-pitcher-id
                         (map (fn [batter-id] {:batter-id batter-id :pitcher-id home-team-pitcher-id}) away-team-roster))
                       (when away-team-pitcher-id
                         (map (fn [batter-id] {:batter-id batter-id :pitcher-id away-team-pitcher-id}) home-team-roster))]))))
         (pmap (fn [bp] (player-stats/batter-vs-pitcher-stats ds (:batter-id bp) (:pitcher-id bp))))
         (pmap score-batter-vs-pitcher)
         (into []))))
