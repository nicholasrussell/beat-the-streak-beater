(ns bts-picker.mlb-api.conference.core
  (:require [bts-picker.mlb-api.client.core :as client]
            [clojure.spec.alpha :as s]))

(def ^:private path-conferences "/v1/conferences")

(defn get-conferences
  []
  (client/get path-conferences))

(s/def :conference/id int?)
(s/def :conference/name string?)
(s/def :conference/link string?)
(s/def :conference/abbreviation string?)
(s/def :conference/hasWildcard boolean?)
(s/def :conference/league map?) ; TODO League type
(s/def :conference/sport map?) ; TODO sport type
(s/def :conference/nameShort string?)
(s/def ::conference (s/keys :req-un [:conference/id
                                     :conference/name
                                     :conference/link
                                     :conference/abbreviation
                                     :conference/hasWildcard
                                     :conference/league
                                     :conference/sport
                                     :conference/nameShort]))
(s/def ::conferences (s/coll-of ::conference))

(s/fdef get-conferences
  :args (s/cat)
  :ret (s/keys :req-un [::conferences]))

