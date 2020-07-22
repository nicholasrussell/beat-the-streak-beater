(ns bts-picker.mlb-api.person.core
  (:require [bts-picker.mlb-api.client.core :as client]
            [clojure.string :as string]))

(def ^:private path-people "/v1/people")
(def ^:private path-person (str path-people "/%s"))
(def ^:private path-game-stats (str path-person "/stats/game/%s"))

(defn get-people
  [person-ids]
  (let [normalized-ids (->> (if (coll? person-ids) person-ids [person-ids])
                            (map str)
                            (string/join ","))]
    (client/get path-people {:query-params {:personIds normalized-ids}})))

(defn get-person
  [person-id]
  (client/get (format path-person person-id)))

(defn get-person-game-stats
  ([person-id game-pk]
   (get-person-game-stats person-id game-pk {}))
  ([person-id game-pk {:keys [group]}]
   (client/get (format path-game-stats person-id game-pk)
               {:query-params {:group group}})))

