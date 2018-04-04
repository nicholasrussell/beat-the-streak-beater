(ns bts-picker.mlb-api.venue.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-venues "/v1/venues")
(def ^:private path-venue (str path-venues "/%s"))

(defn get-venues
  [& {:keys []}]
  (client/get path-venues))

(defn get-venue
  [venue-id & {:keys []}]
  (client/get (format path-venue venue-id)))
