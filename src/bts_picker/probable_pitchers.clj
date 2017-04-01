(ns bts-picker.probable-pitchers
  (:require [clojure.string :as str]
            [java-time :as t] 
            [reaver :refer [parse extract-from attr text]]
            [clojure.tools.trace :refer :all]))

(defn- iso-date->mlb-date
  [date]
  (str/replace date #"\-" "/"))

(defn- get-probable-pitcher-page
  "Date format: YYYY/MM/DD"
  [date]
  (slurp (str "http://mlb.mlb.com/news/probable_pitchers/index.jsp?c_id=mlb&date=" (iso-date->mlb-date date))))

(defn- extract-data-from-page
  [page]
  (extract-from (parse page)
                "div#mc"
                [:pitcher-ids :throws :team-ids :game-ids]
                "div.pitcher" (attr :pid)
                "div.pitcher h5 span" text
                "div.pitcher" (attr :tid)
                "div.pitcher" (attr :gid)))

(defn- data->pitcher-ids
  [extraction-data]
  (map #(if (str/blank? %) :no-data %) (:pitcher-ids (first extraction-data))))

(defn- data->throws
  [extraction-data]
  (:throws (first extraction-data)))

(defn- data->team-ids
  [extraction-data]
  (:team-ids (first extraction-data)))

(defn- data->game-ids
  [extraction-data]
  (mapcat (partial repeat 2) (remove nil? (:game-ids (first extraction-data)))))

(defn- make-probable-pitcher-map
  [pitcher-id throws team-id game-id]
  {:pitcher-id pitcher-id
   :throws (if (= :no-data pitcher-id) :no-data throws)
   :team-id team-id
   :game-id game-id})

(defn- transform-data
  [extraction-data]
  (mapv make-probable-pitcher-map
        (data->pitcher-ids extraction-data)
        (data->throws extraction-data)
        (data->team-ids extraction-data)
        (data->game-ids extraction-data)))

(defn probable-pitchers
  ([] (probable-pitchers (t/format "YYYY-MM-dd" (t/local-date))))
  ([date] (transform-data (extract-data-from-page (get-probable-pitcher-page date)))))

