(ns dev.russell.bts-picker.config)

(def ^:private team-colors-url (atom nil))
(def ^:private weather-api-url (atom nil))
(def ^:private weather-api-key (atom nil))

(defn get-team-colors-url
  []
  @team-colors-url)

(defn get-weather-api-url
  []
  @weather-api-url)

(defn get-weather-api-key
  []
  @weather-api-key)

(defn get-env
  []
  (or (System/getenv "BTS_ENV") "dev"))

(defn get-db-name
  []
  (or (System/getenv "BTS_DB_NAME") "bts"))

(defn get-db-user
  []
  (or (System/getenv "BTS_DB_USER") "postgres"))

(defn get-db-password
  []
  (or (System/getenv "BTS_DB_PASSWORD") "password"))

(defn initialize
  [config]
  (swap! team-colors-url (fn [_ x] x) (:team-colors-url config))
  (swap! weather-api-url (fn [_ x] x) (:weather-api-url config))
  (swap! weather-api-key (fn [_ x] x) (:weather-api-key config)))
