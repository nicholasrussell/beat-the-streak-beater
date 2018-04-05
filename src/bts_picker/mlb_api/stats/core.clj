(ns bts-picker.mlb-api.stats.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-stats "/v1/stats")

(defn get-player-stats
  [person-id stat-types stat-groups]
  (client/get-stats-api path-stats {:query-params {:personId person-id
                                                   :stats stat-types
                                                   :group stat-groups}}))
