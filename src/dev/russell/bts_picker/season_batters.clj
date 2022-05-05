(ns dev.russell.bts-picker.season-batters
  (:require [dev.russell.bts-picker.util :as util]
            [clojure.xml :as xml]))

(def ^:private batter-url "http://gd2.mlb.com/components/game/mlb/year_%s/batters/%s.xml")

(defn- get-batter-url
  [date batter-id]
  (format batter-url (util/date->year date) batter-id))

(defn- get-batter-data
  [date batter-id]
  (assoc (:attrs (xml/parse (get-batter-url date batter-id))) :id batter-id))

(defn- transform-batter-data
  [batter-data]
  batter-data)

(defn batter-season
  ([batter-id] (batter-season (util/now) batter-id))
  ([date batter-id]
   (when-not (nil? batter-id)
     (transform-batter-data (get-batter-data date batter-id)))))
