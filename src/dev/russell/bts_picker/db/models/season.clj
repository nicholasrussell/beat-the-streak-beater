(ns dev.russell.bts-picker.db.models.season
  (:require [java-time]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private upsert-query
"
INSERT INTO seasons (id, spring_start_date, spring_end_date, regular_season_start_date, regular_season_end_date, post_season_start_date, post_season_end_date, current, created_at, updated_at)
VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %b, now(), now())
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
SELECT * FROM seasons WHERE id = '%s';
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
  (jdbc/execute-one! ds
                     [(format upsert-query (:id season) (:spring-start-date season) (:spring-end-date season) (:regular-season-start-date season) (:regular-season-end-date season) (:post-season-start-date season) (:post-season-end-date season) (current? season))]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))

(defn get-by-id
  [ds id]
  (jdbc/execute-one! ds
                     [(format get-by-id-query id)]
                     {:return-keys true :builder-fn rs/as-unqualified-kebab-maps}))
(defn get-current-id
  [ds]
  (:id
   (jdbc/execute-one! ds
                      [get-current-id-query]
                      {:return-keys true :builder-fn rs/as-unqualified-kebab-maps})))
