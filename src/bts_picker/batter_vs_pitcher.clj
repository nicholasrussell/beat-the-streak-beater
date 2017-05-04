(ns bts-picker.batter-vs-pitcher
  (:require [bts-picker.util :as util]
            [clojure.tools.trace :refer :all]))

(def ^:private base-url "http://mlb.mlb.com/lookup/json/named.sit_bvp_5y_date.bam")

(def ^:private bvp-url (str base-url
                            "?vs_pitcher_id=%s"
                            "&game_type='R'"
                            "&team_id=%s"
                            "&date_num='%s'"
                            "&sport_code='mlb'"
                            "&cur_seas_sw='N'"))

(defn- get-bvp-url
  [date team-id pitcher-id]
  (format bvp-url pitcher-id team-id (util/date->mlb-date date)))

(defn- parse-batter-data
  [data]
  (let [batters (:row (:queryResults (:sit_bvp_5y_date data)))]
    batters))

(defn- get-data
  [date team-id pitcher-id]
  (if (= pitcher-id :no-data)
    {}
    (let [raw-data (util/get-json (get-bvp-url date team-id pitcher-id))]
      {:pitcher-id pitcher-id
       :batters (parse-batter-data raw-data)})))

(defn batter-vs-pitcher
  ([team-id pitcher-id] (batter-vs-pitcher (util/now) team-id pitcher-id))
  ([date team-id pitcher-id] (get-data date team-id pitcher-id)))
