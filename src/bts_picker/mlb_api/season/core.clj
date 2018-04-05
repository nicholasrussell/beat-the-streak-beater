(ns bts-picker.mlb-api.season.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-seasons "/v1/seasons")
(def ^:private path-season (str path-seasons "/%s"))

(defn get-seasons
  ([]
   (get-seasons {}))
  ([{:keys [sport-id] :or {sport-id 1}}]
   (client/get-stats-api path-seasons {:query-params {:sportId sport-id}})))

(defn get-season
  ([season-id]
   (get-season season-id))
  ([season-id {:keys [sport-id] :or {sport-id 1}}]
   (client/get-stats-api (format path-season season-id) {:query-params {:sportId sport-id}})))
