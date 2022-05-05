(ns dev.russell.bts-picker.colors
  (:require [clj-http.client :as http-client]
            [dev.russell.bts-picker.config :as config]))

(defn- get-team-color-data
  []
  (let [call-promise (promise)
        delivery-fn (partial deliver call-promise)]
    (http-client/get (config/get-team-colors-url) {:as :json :async? true} delivery-fn delivery-fn)
    call-promise))

(defn team-colors
  [team-name]
  (let [color-data (:body @(get-team-color-data))
        team-data (first (filter #(= team-name (:name %)) color-data))]
    (when team-data
      (:hex (:colors team-data)))))