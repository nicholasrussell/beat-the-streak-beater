(ns bts-picker.daily-batters
  (:require [clojure.tools.trace :refer :all]
            [bts-picker.util :as util]
            [clojure.xml :as xml])
  (:import (java.io FileNotFoundException)))

(def ^:private batter-url "http://gd2.mlb.com/components/game/mlb/year_%s/month_%s/day_%s/batters/%s_%s.xml")

(defn- get-batter-url
  [date batter-id game-num]
  (format batter-url (util/date->year date) (util/date->month date) (util/date->day date) batter-id game-num))

(defn- get-batter-data
  ([date batter-id]
   {:id batter-id
    :games (remove nil? [(get-batter-data date batter-id 1) (get-batter-data date batter-id 2)])}) ; account for double headers
  ([date batter-id game-num]
   (try
     (:attrs (xml/parse (get-batter-url date batter-id game-num)))
     (catch FileNotFoundException e nil))))

(defn- transform-batter-data
  [batter-data]
  (dissoc
   (merge
    batter-data
    (reduce
     (fn [data game]
       {:h (+ (:h data) (read-string (:h game)))
        :ab (+ (:ab data) (read-string (:ab game)))
        :bb (+ (:bb data) (read-string (:bb game)))})
     {:h 0 :ab 0 :bb 0}
     (:games batter-data)))
   :games))

(defn daily-batters
  ([batter-id] (daily-batters (util/now) batter-id))
  ([date batter-id]
   (when-not (nil? batter-id)
     (transform-batter-data (get-batter-data date batter-id)))))
