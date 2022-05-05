(ns dev.russell.bts-picker.rankings.rank)

(defn- combine-scores
  [matchups batter-scores batter-vs-pitcher-scores pitcher-scores]
  (let [scores (reduce (fn [acc batter-score]
                         (assoc acc (:player-id batter-score) {:batter-score (:score batter-score)}))
                       {}
                       batter-scores)
        scores (reduce (fn [acc batter-vs-pitcher-scores]
                         (assoc
                          acc
                          (:player-id batter-vs-pitcher-scores)
                          (if-let [existing (get acc (:player-id batter-vs-pitcher-scores))]
                            (merge existing {:batter-vs-pitcher-score (:score batter-vs-pitcher-scores)})
                            {:batter-vs-pitcher-score (:score batter-vs-pitcher-scores)})))
                       scores
                       batter-vs-pitcher-scores)
        scores (let [pitcher-scores
                     (reduce (fn [acc pitcher-score]
                               (let [other-team-roster (reduce
                                                        (fn [_ matchup]
                                                          (if (= (-> matchup :teams :away :pitcher :player-id)
                                                                 (:player-id pitcher-score))
                                                            (reduced (->> matchup :teams :home :roster (map :player-id)))
                                                            (if (= (-> matchup :teams :home :pitcher :player-id)
                                                                   (:player-id pitcher-score))
                                                              (reduced (->> matchup :teams :away :roster (map :player-id)))
                                                              nil)))
                                                        nil
                                                        @matchups)]
                                 (if (nil? other-team-roster)
                                   acc
                                   (concat
                                    acc
                                    (map (fn [batter-id]
                                           {:player-id batter-id
                                            :score (:score pitcher-score)})
                                         other-team-roster)))))
                             []
                             pitcher-scores)]
                 (reduce
                  (fn [acc pitcher-score]
                    (if-let [existing (get acc (:player-id pitcher-score))]
                      (assoc acc (:player-id pitcher-score) (merge existing {:pitcher-score (:score pitcher-score)}))
                      acc))
                  scores
                  pitcher-scores))]
    (reduce-kv
     (fn [acc k v]
       (let [score {:player-id k
                    :batter-score (or (:batter-score v) 0.0)
                    :batter-vs-pitcher-score (or (:batter-vs-pitcher-score v) 0.0)
                    :pitcher-score (or (:pitcher-score v) 0.0)}]
         (conj acc score)))
     []
     scores)))

(defn- sum-score
  [scores]
  (reduce + 0 scores))

(defn- score-batter
  [score]
  {:player-id (:player-id score)
   :calculated-scores {:batter-score (:batter-score score)
                       :batter-vs-pitcher-score (:batter-vs-pitcher-score score)
                       :pitcher-score (:pitcher-score score)}
   :score (sum-score [(double (* (:batter-score score) (/ 5.0 7.0)))
                      (double (* (:batter-vs-pitcher-score score) (/ 1.0 7.0)))
                      (double (* (:pitcher-score score) (/ 1.0 7.0)))])})

(defn rank-batters
  [matchups batter-scores batter-vs-pitcher-scores pitcher-scores]
  (let [scores (mapv score-batter (combine-scores matchups batter-scores batter-vs-pitcher-scores pitcher-scores))]
    (sort-by :score #(compare %2 %1) scores)))