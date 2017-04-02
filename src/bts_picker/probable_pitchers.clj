(ns bts-picker.probable-pitchers
  (:require [bts-picker.util :as util]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [reaver :refer [parse extract-from attr text]]
            [clojure.tools.trace :refer :all]))

(defn- get-probable-pitcher-page
  [date]
  (slurp (str "http://mlb.mlb.com/news/probable_pitchers/index.jsp?c_id=mlb&date=" (util/date->mlb-date date))))

(defn- extract-data-from-page
  [page]
  (extract-from (parse page)
                "div#mc"
                [:pitcher-ids :throws :names :team-ids :game-ids]
                "div.pitcher" (attr :pid)
                "div.pitcher h5 span" text
                "div.pitcher h5 a" text
                "div.pitcher" (attr :tid)
                "div.pitcher" (attr :gid)))

(defn- data->pitcher-ids
  [extraction-data]
  (map #(if (str/blank? %) :no-data %) (:pitcher-ids (first extraction-data))))

(defn- data->throws
  [extraction-data]
  (:throws (first extraction-data)))

(defn- data->names
  [extraction-data]
  (:names (first extraction-data)))

(defn- data->team-ids
  [extraction-data]
  (:team-ids (first extraction-data)))

(defn- data->game-ids
  [extraction-data]
  (mapcat (partial repeat 2) (remove nil? (:game-ids (first extraction-data)))))

(defn- make-probable-pitcher-map
  [pitcher-id throws name team-id game-id]
  {:pitcher-id pitcher-id
   :throws (if (= :no-data pitcher-id) :no-data throws)
   :name (if (= :no-data pitcher-id) :no-data name)
   :team-id team-id
   :game-id game-id})

(defn- transform-data
  [extraction-data]
  (mapv make-probable-pitcher-map
        (data->pitcher-ids extraction-data)
        (data->throws extraction-data)
        (data->names extraction-data)
        (data->team-ids extraction-data)
        (data->game-ids extraction-data)))

(def ^:private pitcher-base-url "http://gd2.mlb.com/components/game/mlb/year_%s/pitchers")

(defn- pitcher-url
  [date pitcher-id]
  (str (format pitcher-base-url (util/date->year date)) "/" pitcher-id ".xml"))

(defn- supplement-pitcher-data
  [date probable-pitchers]
  (mapv
   (fn [pitcher]
     (if-not (= :no-data (:pitcher-id pitcher))
       (let [x (xml/parse (pitcher-url date (:pitcher-id pitcher)))]
         (merge pitcher (dissoc (:attrs x) :game_id :game_pk)))
       pitcher))
   probable-pitchers))

(defn probable-pitchers
  ([] (probable-pitchers (util/now)))
  ([date] (supplement-pitcher-data date (transform-data (extract-data-from-page (get-probable-pitcher-page date))))))

