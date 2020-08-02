(ns bts-picker.config)

(def ^:private team-colors-url (atom nil))
(def ^:private weather-api-url (atom nil))
(def ^:private weather-api-key (atom nil))

(defn team-colors-url
  [])

(defn weather-api-url
  []
  @weather-api-url)

(defn weather-api-key
  []
  @weather-api-key)

(defn initialize
  [config]
  (swap! team-colors-url (:team-colors-url config))
  (swap! weather-api-url (:weather-api-url config))
  (swap! weather-api-key (:weather-api-key config)))

