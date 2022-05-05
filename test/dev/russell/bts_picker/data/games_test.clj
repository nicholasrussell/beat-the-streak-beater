(ns dev.russell.bts-picker.data.games-test
  (:require [clojure.test :as t]
            [dev.russell.bts-picker.data.games :as impl]
            [dev.russell.bts-picker.util.core :as date-util]))

(t/deftest get-games-test
  (t/testing "gets game ids for date"
    (t/is (= ["446277" (impl/get-games "2015-11-1")]))
    (t/is (= ["446277" (impl/get-games (date-util/of 2015 11 1))]))))

(t/deftest get-game-test
  (t/testing "gets game data"
    (t/is (not (nil? (impl/get-game "446277")))))) ; TODO
