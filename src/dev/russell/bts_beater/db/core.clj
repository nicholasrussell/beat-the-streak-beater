(ns dev.russell.bts-beater.db.core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.date-time]
            [dev.russell.bts-beater.config :as config]
            [next.jdbc.result-set :as rs]))

(def ^:private default-ds-opts {:return-keys true
                                :builder-fn rs/as-unqualified-kebab-maps})

(defn db-name
  []
  (let [root-db-name (config/get-db-name)]
    (if (= (config/get-env) "test")
      (str root-db-name "_test")
      root-db-name)))

(defn db-spec
  []
  {:dbtype "postgresql"
   :dbname (db-name)
   :user (config/get-db-user)
   :password (config/get-db-password)})

(defn get-datasource
  []
  (jdbc/get-datasource (db-spec)))

(defn execute!
  [ds query]
  (jdbc/execute! ds query default-ds-opts))

(defn execute-one!
  [ds query]
  (jdbc/execute-one! ds query default-ds-opts))

(defn execute-batch!
  [ds query params]
  (jdbc/execute-batch! ds query params default-ds-opts))

