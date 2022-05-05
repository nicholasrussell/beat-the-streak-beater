(ns dev.russell.bts-picker.rankings.pitcher
  (:require [dev.russell.batboy.people.core :as people]))

(defn- player-pitching-stats
  [player-id]
  (future
    {:player-id player-id
     :raw-stats (->> @(people/get-person-stats {:path-params {:id player-id}
                                                :query-params {:stats "season,career,yearByYear"
                                                               :group "pitching"}})
                     :body
                     :stats)}))

(defn- season-hits
  [stats]
  (or (:hits stats) 0))

(defn- season-hit-bf-percentage
  [stats]
  (if (or (nil? (:battersFaced stats)) (<= (:battersFaced stats) 0))
    0.0
    (double (/ (:hits stats) (:battersFaced stats)))))

(defn- range-normalized-score
  [score min max]
  (if (or (nil? score) (nil? min) (nil? max) (= (- max min) 0))
    0.0
    (double (/ (- score min) (- max min)))))

(defn- weight
  [key score stat-totals]
  (condp = key
    :season-hits (* (range-normalized-score score (:season-hits-min stat-totals) (:season-hits-max stat-totals)) (/ 1.0 7.0))
    :season-hit-bf-percentage (* score (/ 6.0 7.0))
    nil))

(defn- apply-weights
  [scores stat-totals]
  (reduce-kv
   (fn [m k v]
     (let [w (weight k v stat-totals)]
       (if w
         (assoc m k w)
         m)))
   {}
   scores))

(defn- sum-score
  [scores]
  (reduce + 0 scores))

(defn- player-score
  [player-id raw-scores stat-totals]
  (let [weighted-scores (apply-weights raw-scores stat-totals)]
    {:player-id player-id
     :score-metadata {:raw-scores raw-scores
                      :weighted-scores weighted-scores}
     :score (sum-score (vals weighted-scores))}))

(defn- score-pitcher
  [player-stats stat-totals]
  (future
    (player-score (:player-id player-stats)
                  (:raw-scores player-stats)
                  stat-totals)))

(defn- raw-pitcher-score
  [player-stats]
  (future
    (let [player-id (:player-id @player-stats)
          season-stats (->> (:raw-stats @player-stats) (filter #(= (:displayName (:type %)) "season")) first :splits first :stat)]
      {:player-id player-id
       :raw-scores {:season-hits (season-hits season-stats)
                    :season-hit-bf-percentage (season-hit-bf-percentage season-stats)}})))

(defn- calc-stat-total
  [fn stat-total raw-stat]
  (if (nil? stat-total)
    raw-stat
    (if (nil? raw-stat)
      stat-total
      (fn stat-total raw-stat))))

(defn score-pitchers
  [player-ids]
  (let [raw-with-totals
        (->>
         player-ids
         (pmap player-pitching-stats)
         (pmap raw-pitcher-score)
         (map deref) ; TODO realizes futures for now just to make the totaling easier :shrug:
         (reduce (fn [acc raw-scores]
                   (let [stat-totals {:season-hits-min (calc-stat-total min (-> acc :stat-totals :season-hits-min) (-> raw-scores :raw-scores :season-hits))
                                      :season-hits-max (calc-stat-total max (-> acc :stat-totals :season-hits-max) (-> raw-scores :raw-scores :season-hits))
                                      :season-hit-bf-percentage-min (calc-stat-total min (-> acc :stat-totals :season-hit-bf-percentage-min) (-> raw-scores :raw-scores :season-hit-bf-percentage))
                                      :season-hit-bf-percentage-max (calc-stat-total max (-> acc :stat-totals :season-hit-bf-percentage-max) (-> raw-scores :raw-scores :season-hit-bf-percentage))}]
                     {:stat-totals stat-totals
                      :pitcher-stats (conj (:pitcher-stats acc) raw-scores)}))
                 {:stat-totals {:season-hits-min 9999
                                :season-hits-max 0
                                :season-hit-bf-percentage-min 1
                                :season-hit-bf-percentage-max 0}
                  :pitcher-stats []}))]
    (map (fn [pitcher-stats] (score-pitcher pitcher-stats (:stat-totals raw-with-totals))) (:pitcher-stats raw-with-totals))))
