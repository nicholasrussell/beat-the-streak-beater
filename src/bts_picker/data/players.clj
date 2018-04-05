(ns bts-picker.data.players
  (:require [bts-picker.mlb-api.person.core :as api-person]))

(defn- transform-player-data
  [player-data]
  {:id (some-> player-data :id str)
   :name (some-> player-data :nameFirstLast)
   :position (some-> player-data :primaryPosition)
   :bats (some-> player-data :batSide)
   :throws (some-> player-data :pitchHand)})

(defn players
  [player-ids]
  (some->> player-ids api-person/get-people :people (map transform-player-data)))

(defn pitcher?
  [player]
  (= "1" (some-> player :position :code)))

(defn pitchers
  [players]
  (filter pitcher? players))
