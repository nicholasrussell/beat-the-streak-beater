(ns dev.russell.bts-beater.db.models.sky
  (:require [dev.russell.bts-beater.db.core :as db-core]))

(def ^:private upsert-query
  "
INSERT INTO skies (code, description, created_at, updated_at)
VALUES(?, ?, now(), now())
ON CONFLICT (code)
DO UPDATE SET
 description = EXCLUDED.description,
 updated_at = EXCLUDED.updated_at;
")

(def ^:private get-by-code-query
  "
SELECT * FROM skies WHERE code = ?;
")

(defn upsert
  [ds sky]
  (db-core/execute-one!
   ds
   [upsert-query (:code sky) (:description sky)]))

(defn upsert-batch
  [ds skies]
  (db-core/execute-batch!
   ds
   upsert-query
   (mapv (fn [sky] [(:code sky) (:description sky)]) skies)))

(defn get-by-code
  [ds code]
  (db-core/execute-one!
   ds
   [get-by-code-query code]))
