(ns bts-picker.mlb-api.game.core
  (:require [bts-picker.mlb-api.client.core :as client]))

(def ^:private path-game-1_1 "/v1.1/game/%s")
(def ^:private path-game-1 "/v1/game/%s")
(def ^:private path-live-feed (str path-game-1_1 "/feed/live"))
(def ^:private path-live-feed-diff-patch (str path-live-feed "/diffPatch"))
(def ^:private path-live-feed-timestamps (str path-live-feed "/timestamps"))
(def ^:private path-context-metrics (str path-game-1 "/contextMetrics"))
(def ^:private path-win-probability (str path-game-1 "/winProbability"))
(def ^:private path-box-score (str path-game-1 "/boxscore"))
(def ^:private path-line-score (str path-game-1 "/linescore"))
(def ^:private path-play-by-play (str path-game-1 "/playByPlay"))

(defn get-live-feed
  [game-pk & {:keys [timecode]}]
  (client/get (format path-live-feed game-pk) {:query-params (when timecode {:timecode timecode})}))

(defn get-live-feed-diff-patch
  [game-pk & {:keys [start-timecode end-timecode]}]
  (client/get (format path-live-feed-diff-patch game-pk)
              {:query-params {:startTimecode start-timecode :endTimecode end-timecode}}))

(defn get-live-feed-timestamps
  [game-pk & {:keys []}]
  (client/get (format path-live-feed-timestamps game-pk)))

(defn get-context-metrics
  [game-pk & {:keys []}]
  (client/get (format path-context-metrics game-pk)))

(defn get-win-probability
  [game-pk & {:keys []}]
  (client/get (format path-win-probability game-pk)))

(defn get-box-score
  [game-pk & {:keys []}]
  (client/get (format path-box-score game-pk)))

(defn get-line-score
  [game-pk & {:keys []}]
  (client/get (format path-line-score game-pk)))

(defn get-play-by-play
  [game-pk & {:keys []}]
  (client/get (format path-play-by-play game-pk)))
