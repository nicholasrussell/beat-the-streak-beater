(ns dev.russell.bts-picker.db.core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.date-time]
            [dev.russell.bts-picker.config :as config]))

(defn db-name
  []
  (let [root-db-name (config/get-db-name)]
    (if (= (config/get-env) "test")
      (str root-db-name "_test")
      root-db-name)))

(defn db-spec
  []
  {:dbtype "postgresql" :dbname (db-name) :user (config/get-db-user) :password (config/get-db-password)})

(defn get-datasource
  []
  (jdbc/get-datasource (db-spec)))
