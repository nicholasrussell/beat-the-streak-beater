(ns bts-picker.mlb-api.game.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-game-v1_1 "/v1.1/game/%s")
(def ^:private path-game-v1 "/v1/game/%s")
(def ^:private path-live-feed (str path-game-v1_1 "/feed/live"))
(def ^:private path-live-feed-diff-patch (str path-live-feed "/diffPatch"))
(def ^:private path-live-feed-timestamps (str path-live-feed "/timestamps"))
(def ^:private path-context-metrics (str path-game-v1 "/contextMetrics"))
(def ^:private path-win-probability (str path-game-v1 "/winProbability"))
(def ^:private path-box-score (str path-game-v1 "/boxscore"))
(def ^:private path-line-score (str path-game-v1 "/linescore"))
(def ^:private path-play-by-play (str path-game-v1 "/playByPlay"))

(defn get-live-feed
  ([game-pk]
   (get-live-feed game-pk {}))
  ([game-pk {:keys [timecode]}]
   (client/get (format path-live-feed game-pk) {:query-params {:timecode timecode}})))

(defn get-live-feed-diff-patch
  ([game-pk]
   (client/get game-pk {}))
  ([game-pk {:keys [start-timecode end-timecode]}]
   (client/get (format path-live-feed-diff-patch game-pk)
               {:query-params {:startTimecode start-timecode :endTimecode end-timecode}})))

(defn get-live-feed-timestamps
  [game-pk]
  (client/get (format path-live-feed-timestamps game-pk)))

(defn get-context-metrics
  [game-pk]
  (client/get (format path-context-metrics game-pk)))

(defn get-win-probability
  [game-pk]
  (client/get (format path-win-probability game-pk)))

(defn get-box-score
  [game-pk]
  (client/get (format path-box-score game-pk)))

(defn get-line-score
  [game-pk]
  (client/get (format path-line-score game-pk)))

(defn get-play-by-play
  [game-pk]
  (client/get (format path-play-by-play game-pk)))
