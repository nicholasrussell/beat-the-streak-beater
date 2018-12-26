(ns bts-picker.mlb-api.awards.core
  (:require [bts-picker.mlb-api.client.core :as client]
            [clojure.spec.alpha :as s]))

(def ^:private path-awards "/v1/awards")
(def ^:private path-award (str path-awards "/%s"))
(def ^:private path-award-recipients (str path-award "/recipients"))

(defn get-awards
  []
  (:awards )(client/get path-awards))

(defn get-award
  [award-id]
  (some-> (client/get (format path-award award-id)) :awards first))

(defn get-award-recipients
  [award-id]
  (:awards (client/get (format path-award-recipients award-id))))

(s/def :award/id string?)
(s/def :award/name string?)
(s/def :award/description string?)
(s/def :award/sortOrder int?) ; TODO sort order
(s/def :award/league map?) ; TODO league type
(s/def ::award (s/keys :req-un [:award/id
                                :award/name
                                :award/description
                                :award/league]
                       :opt-un [:award/sortOrder]))

(s/def :recipient-award/id :award/id)
(s/def :recipient-award/name :award/name)
(s/def :recipient-award/date string?) ; TODO iso date string
(s/def :recipient-award/season string?) ; TODO season
(s/def :recipient-award/team map?) ; TODO team type
(s/def :recipient-award/player map?) ; TODO player type
(s/def ::recipient-award (s/keys :req-un [:recipient-award/id
                                          :recipient-award/name
                                          :recipient-award/date
                                          :recipient-award/season
                                          :recipient-award/team
                                          :recipient-award/player]))

(s/def ::awards (s/or
                 :award (s/coll-of ::award)
                 :recipient-award (s/coll-of ::recipient-award)))

(s/fdef get-awards
  :args (s/cat)
  :ret (s/keys :req-un [::awards]))

(s/fdef get-award
  :args (s/cat :award-id :award/id)
  :ret ::award)

(s/fdef get-award-recipients
  :args (s/cat :award-id :award/id)
  :ret (s/keys :req-un [::awards]))

