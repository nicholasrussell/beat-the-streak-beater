(ns bts-picker.mlb-api.client.core
  (:require [clj-http.client :as http-client]
            [cheshire.core :as cheshire]
            [clojure.tools.trace :as trace]
            [clojure.spec.alpha :as s]))

(def ^:private debug false)
(def ^:private base-url-stats-api "http://statsapi.mlb.com/api")

(defn- make-stats-api-url
  [path]
  (format "%s/%s" base-url-stats-api path))

(defn get-stats-api
  ([path]
   (get-stats-api path {}))
  ([path {:keys [query-params]}]
    ; TODO handle exceptions
   (some->
     (http-client/get
       (make-stats-api-url path)
       {:query-params query-params
        :as :json
        :debug debug})
     :body)))

(s/fdef get-stats-api
        :args (s/or :path (s/cat :path string?)
                    :path-options (s/cat :path string?
                                         :options map?))
        :ret map?)
