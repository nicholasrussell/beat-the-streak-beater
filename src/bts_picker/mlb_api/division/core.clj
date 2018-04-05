(ns bts-picker.mlb-api.division.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-divisions "/v1/divisions")
(def ^:private path-division (str path-divisions "/%s"))

(defn get-divisions
  ([]
   (get-divisions {}))
  ([{:keys [sport-id]}]
   (client/get-stats-api path-divisions
                         {:query-params (when sport-id {:sportId sport-id})})))

(defn get-division
  [division-id]
  (client/get-stats-api (format path-division division-id)))