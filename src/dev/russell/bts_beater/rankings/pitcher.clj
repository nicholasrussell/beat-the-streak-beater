(ns dev.russell.bts-beater.rankings.pitcher
  (:require [dev.russell.bts-beater.db.core :as db]
            [dev.russell.bts-beater.db.models.player-stats :as player-stats]))

(defn- range-normalized-score
  [score min max]
  (if (or (nil? score) (nil? min) (nil? max) (= (- max min) 0))
    0.0
    (double (/ (- score min) (- max min)))))

(defn- weight
  [season-aggregates stat value]
  (condp = stat
    :hits (* (range-normalized-score value (:hits-min season-aggregates) (:hits-max season-aggregates)) (/ 3.0 7.0))
    :hits-per-batter-faced (* value (/ 4.0 7.0))
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

(defn- score-pitcher
  [season-aggregates player-season-stats]
  (let [weighted-scores (apply-weights season-aggregates player-season-stats)]
    {:player-id (:player-id player-season-stats)
     :score-metadata {:raw-scores player-season-stats
                      :weighted-scores weighted-scores}
     :score (sum-score (vals weighted-scores))}))

(defn score-pitchers
  [season player-ids]
  (let [ds (db/get-datasource)
        season-aggregates (player-stats/season-pitching-aggregates ds season)
        stats (player-stats/player-season-pitching-stats ds season player-ids)]
    (->> stats
         (pmap (partial score-pitcher season-aggregates))
         (into []))))
   
