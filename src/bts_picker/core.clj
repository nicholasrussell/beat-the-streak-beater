(ns bts-picker.core
  (:require [clj-http.client :as http-client]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [bts-picker.probable-pitchers :as probable-pitchers]))

(defn- trace
  [body]
  (do (prn body) body))

(def war-daily-batter-csv "./war-daily-batter.csv")

(defn retrieve-daily-batter-war-data
  []
  (:body (http-client/get "http://www.baseball-reference.com/data/war_daily_bat.txt")))

(defn write-batter-war-data-to-file
  [f data]
  (spit f data))

(defn parse-csv-data
  [f]
  (with-open [in-file (io/reader f)]
    (doall
     (csv/read-csv in-file))))

(defn csv-data-to-map
  [data]
  (let [headers (map keyword (first data))]
    (map (fn [row] (apply assoc {} (interleave headers row))) (rest data))))

(defn str->num
  [str]
  (let [num (read-string str)]
    (if (number? num)
      num
      0)))

(defn -main
  [& args]
  ;(trace (probable-pitchers/probable-pitcher-ids "2017/03/31"))
  (let [daily-war-batter-data (retrieve-daily-batter-war-data)]
    (println "Got batter data")
    (write-batter-war-data-to-file war-daily-batter-csv daily-war-batter-data)
    (let [war-data (parse-csv-data war-daily-batter-csv)
          csv-map (csv-data-to-map war-data)
          current-year-players (filter #(= (:year_ID %) "2016") csv-map)
          fix-war-nums (map #(update-in % [:WAR_off] str->num) current-year-players)
          fix-pa-nums (map #(update-in % [:PA] str->num) fix-war-nums)
          sorted (sort-by (juxt :WAR_off :PA) fix-pa-nums)
          ranked (reverse sorted)]
      (doseq [player (take 10 (map (fn [player] {:war (:WAR_off player) :pa (:PA player) :name (:name_common player)}) ranked))] (println player)))))
