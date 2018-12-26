(ns bts-picker.mlb-api.stats.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-stats "/v1/stats")
(def ^:private path-stats-leaders (str path-stats "/leaders"))

(defn get-player-stats
  [person-id stat-types stat-groups]
  (client/get path-stats {:query-params {:personId person-id
                                         :stats stat-types
                                         :group stat-groups}}))
(defn get-stats-leaders
  []
  (client/get path-stats-leaders))

