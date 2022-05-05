(ns dev.russell.bts-picker.stats)

(defn range-normalized-score
  [score-set]
  (let [min-score (apply min (map :score score-set))
        max-score (apply max (map :score score-set))]
    (mapv
     (fn [element]
       (let [normalized-score (/ (- (:score element) min-score) (- max-score min-score))]
         (assoc element :normalized-score normalized-score)))
     score-set)))