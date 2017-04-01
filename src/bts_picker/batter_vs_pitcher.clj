(ns bts-picker.batter-vs-pitcher
  (:require [bts-picker.util :as util]
            [clojure.tools.trace :refer :all]))

(def ^:private base-url "http://mlb.mlb.com/lookup/json/named.sit_bvp_5y_date.bam")

(def ^:private bvp-url (str base-url
                            "?vs_pitcher_id=%s"
                            "&game_type=%27R%27"
                            "&team_id=%s"
                            "&date_num=%27%s%27"
                            "&sport_code=%27mlb%27"
                            "&cur_seas_sw=%27N%27"))

(defn get-bvp-url
  [date team-id pitcher-id]
  (format (trace bvp-url) pitcher-id team-id (trace (util/date->mlb-date date))))

(defn- get-data
  [date team-id pitcher-id]
  (let [raw-data (util/get-json (get-bvp-url date team-id pitcher-id))]
    raw-data))

(defn batter-vs-pitcher
  ([team-id pitcher-id] (batter-vs-pitcher (util/now) team-id pitcher-id))
  ([date team-id pitcher-id] (trace (get-data date team-id pitcher-id))))
