(ns bts-picker.mlb-api.client.core
  (:require [clj-http.client :as http-client]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [com.rpl.specter :as sp])
  (:refer-clojure :exclude [get])
  (:import (java.net URI)))

(def ^{:private true :dynamic true} *debug* false)
(def ^:private stats-api-base-url "http://statsapi.mlb.com/api")

(defn- make-stats-api-url
  [path]
  (URI. (str stats-api-base-url path)))

(defn- make-request-context
  [options]
  (merge {:as :json
          :debug *debug*
          :debug-body *debug*}
         options))

(defn- clojurize-keys
  [[k v]]
  (let [v (if (string? v)
            (condp = v
              "Y" true
              "N" false
              v)
            v)
        sausage-key (->>
                     (-> (if (keyword? k) (name k) k)
                         (string/split #"(?=[A-Z])"))
                     (map string/lower-case)
                     (string/join "-"))
        boolified (if (boolean? v) (str sausage-key "?") sausage-key)
        key (keyword (str boolified))]
    [key v]))

(defn- transform-response
  [response]
  (select-keys
   (sp/transform [:body]
                 (fn [body]
                   (walk/postwalk
                    (fn [x]
                      (if (map? x)
                        (->> x
                             (map clojurize-keys)
                             (into {}))
                        x))
                    body))
                 response)
   [:status :headers :body]))

(defn get
  ([path]
   (get path {}))
  ([path {:keys [query-params] :as context}]
   (some->
    (http-client/get
     (-> path make-stats-api-url str)
     (make-request-context context))
    transform-response)))

(s/def ::path (s/and string?
                     #(string/starts-with? % "/")
                     #(= (str "/api" %) (.getPath (make-stats-api-url %)))))
(s/def ::query-params (s/nilable map?))
(s/def ::options (s/keys :opt-un [::query-params]))

(s/def ::get-args (s/or :path (s/cat :path ::path)
                        :path-with-options (s/cat :path ::path
                                                  :options ::options)))
(s/def ::get-ret (s/or :object map?
                       :array (s/coll-of map?)))

(s/fdef get
        :args ::get-args
        :ret ::get-ret)

