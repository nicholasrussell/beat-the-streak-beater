(ns bts-picker.mlb-api.venue.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-venues "/v1/venues")
(def ^:private path-venue (str path-venues "/%s"))

(defn get-venues
  []
  (client/get path-venues))

(defn get-venue
  [venue-id]
  (client/get (format path-venue venue-id)))
