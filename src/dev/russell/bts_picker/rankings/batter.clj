(ns dev.russell.bts-picker.rankings.batter
  (:require [dev.russell.batboy.people.core :as people]
            [dev.russell.bts-picker.db.core :as db]
            [dev.russell.bts-picker.db.models.season :as season]
            [dev.russell.bts-picker.db.models.player-stats :as player-stats]))

(defn- range-normalized-score
  [score min max]
  (if (or (nil? score) (nil? min) (nil? max) (= (- max min) 0))
    0.0
    (double (/ (- score min) (- max min)))))

(defn- weight
  [season-aggregates stat value]
  (condp = stat
    :hits (* (range-normalized-score value (:hits-min season-aggregates) (:hits-max season-aggregates)) (/ 4.0 7.0))
    :hits-percentage (* value (/ 3.0 7.0))
    nil))

(defn- apply-weights
  [season-aggregates player-season-stats]
  (reduce-kv
   (fn [m k v]
     (let [w (weight season-aggregates k v)]
       (if w
         (assoc m k w)
         m)))
   {}
   player-season-stats))

(defn- sum-score
  [scores]
  (reduce + 0 scores))

(defn- score-batter
  [season-aggregates player-season-stats]
  (let [weighted-scores (apply-weights season-aggregates player-season-stats)]
    {:player-id (:player-id player-season-stats)
     :score-metadata {:raw-scores player-season-stats
                      :weighted-scores weighted-scores}
     :score (sum-score (vals weighted-scores))}))

(defn score-batters
  [season player-ids]
  (let [ds (db/get-datasource)
        season-aggregates (player-stats/season-hits-aggregates ds season)
        stats (player-stats/player-season-batting-stats ds season player-ids)]
    (->> stats
         (pmap (partial score-batter season-aggregates))
         (into []))))

