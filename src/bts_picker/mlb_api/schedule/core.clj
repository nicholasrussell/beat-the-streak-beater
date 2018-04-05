(ns bts-picker.mlb-api.schedule.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-schedule "/v1/schedule")

(defn get-schedule
  ([]
   (get-schedule {}))
  ([{:keys [sport-id date] :or {sport-id 1}}]
   (client/get-stats-api path-schedule {:query-params {:sportId sport-id
                                                       :date date}})))
