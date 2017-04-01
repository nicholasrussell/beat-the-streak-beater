(ns bts-picker.core
  (:require [clojure.tools.trace :refer :all]
            [bts-picker.games]
            [bts-picker.probable-pitchers]))

(defn- pitchers-for-games
  [games probable-pitchers]
  (mapv
   (fn [game]
     (let [pitchers (filterv #(= (:game-id %) (:id game)) probable-pitchers)]
       (assoc game :probable-pitchers pitchers)))
   games))

(defn -main
  [& args]
  (let [games (bts-picker.games/games)
        probable-pitchers (bts-picker.probable-pitchers/probable-pitchers)]
    (trace (pitchers-for-games games probable-pitchers))))
    
