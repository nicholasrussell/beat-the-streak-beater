(ns dev.russell.bts-beater.db.models.season
  (:require [java-time]
            [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO seasons (id, spring_start_date, spring_end_date, regular_season_start_date, regular_season_end_date, post_season_start_date, post_season_end_date, current, created_at, updated_at)
VALUES(?, ?, ?, ?, ?, ?, ?, ?, now(), now())
ON CONFLICT (id)
DO UPDATE SET
 spring_start_date = EXCLUDED.spring_start_date,
 spring_end_date = EXCLUDED.spring_end_date,
 regular_season_start_date = EXCLUDED.regular_season_start_date,
 regular_season_end_date = EXCLUDED.regular_season_end_date,
 post_season_start_date = EXCLUDED.post_season_start_date,
 post_season_end_date = EXCLUDED.post_season_end_date,
 current = EXCLUDED.current,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-id-query
  "
SELECT * FROM seasons WHERE id = ?;
")

(def ^:private get-current-id-query
  "
SELECT id FROM seasons WHERE current = true;
")

(defn- current?
  [season]
                                        ; close enough
  (= (str (.getYear (java-time/local-date))) (:id season)))

(defn upsert
  [ds season]
  (db-core/execute-one!
   ds
   [upsert-query (:id season) (:spring-start-date season) (:spring-end-date season) (:regular-season-start-date season) (:regular-season-end-date season) (:post-season-start-date season) (:post-season-end-date season) (current? season)]))

(defn upsert-batch
  [ds seasons]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [season] [(:id season) (:spring-start-date season) (:spring-end-date season) (:regular-season-start-date season) (:regular-season-end-date season) (:post-season-start-date season) (:post-season-end-date season) (current? season)]) seasons)))

(defn get-by-id
  [ds id]
  (db-core/execute-one!
   ds
   [get-by-id-query id]))

(defn get-current-id
  [ds]
  (:id
   (db-core/execute-one!
    ds
    [get-current-id-query])))
