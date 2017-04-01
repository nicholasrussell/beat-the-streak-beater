(ns bts-picker.core
  (:require [clojure.tools.trace :refer :all]
            [clojure.string :as str]
            [bts-picker.games]
            [bts-picker.probable-pitchers]))

(defn- pitchers-for-games
  [games probable-pitchers]
  (mapv
   (fn [game]
     (let [pitchers (filterv #(= (:game-id %) (:id game)) probable-pitchers)]
       (assoc game :probable-pitchers pitchers)))
   games))

(defn- pitcher-heuristic
  [p1 p2]
  (let [p1h (if (contains? p1 :s_h) (read-string (:s_h p1)) 0)
        p1ip (if (contains? p1 :s_ip) (read-string (:s_ip p1)) 0)
        p2h (if (contains? p2 :s_h) (read-string (:s_h p2)) 0)
        p2ip (if (contains? p2 :s_ip) (read-string (:s_ip p2)) 0)
        p1r (if (> p1ip 0) (/ p1h p1ip) 1)
        p2r (if (> p2ip 0) (/ p2h p2ip) 1)]
    (compare p2r p1r)))

(defn- rank-pitchers
  [pitchers]
  (sort pitcher-heuristic pitchers))

(defn -main
  [& args]
  (let [games (bts-picker.games/games "2017-04-03")
        probable-pitchers (bts-picker.probable-pitchers/probable-pitchers "2017-04-03")
        games-with-pitchers (pitchers-for-games games probable-pitchers)
        worst-pitchers (trace (rank-pitchers probable-pitchers))]
    games-with-pitchers))
    
