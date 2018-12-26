(ns bts-picker.mlb-api.client.core
  (:require [clj-http.client :as http-client]
            [clojure.spec.alpha :as s]
            [clojure.string :as string])
  (:refer-clojure :exclude [get])
  (:import (java.net URI)))

(def ^{:private true :dynamic true} *debug* false)
(def ^:private stats-api-base-url "http://statsapi.mlb.com/api")

(defn- make-stats-api-url
  [path]
  (URI. (str stats-api-base-url path)))

(defn get
  ([path]
   (get path {}))
  ([path {:keys [query-params]}]
   (some->
    (http-client/get
     (-> path make-stats-api-url str)
     {:query-params query-params
      :as :json
      :debug *debug*
      :debug-body *debug*})
    :body)))

(s/def ::path (s/and string?
                     #(string/starts-with? % "/")
                     #(= (str "/api" %) (.getPath (make-stats-api-url %)))))
(s/def ::query-params (s/nilable map?))
(s/def ::options (s/keys :opt-un [::query-params]))

(s/fdef get
        :args (s/or :path (s/cat :path ::path)
                    :path-with-options (s/cat :path ::path
                                              :options ::options))
        :ret (s/or :object map?
                   :array coll?))
