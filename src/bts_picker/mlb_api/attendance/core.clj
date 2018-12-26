(ns bts-picker.mlb-api.attendance.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-attendance "/v1/attendance")

(defn get-attendance
  [{:keys [team-id league-id]}]
  (client/get path-attendance {:query-params {:teamId team-id :leagueId league-id}}))

