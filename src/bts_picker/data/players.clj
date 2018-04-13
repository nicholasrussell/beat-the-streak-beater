(ns bts-picker.data.players
  (:require [bts-picker.mlb-api.person.core :as api-person]))

(defn- transform-player-data
  [player-data]
  {:id (some-> player-data :id str)
   :name (:nameFirstLast player-data)
   :position (:primaryPosition player-data)
   :bats (:batSide player-data)
   :throws (:pitchHand player-data)})

(defn players
  [player-ids]
  (some->> player-ids api-person/get-people :people (map transform-player-data)))

(defn pitcher?
  [player]
  (= "1" (some-> player :position :code)))

(defn pitchers
  [players]
  (filter pitcher? players))
