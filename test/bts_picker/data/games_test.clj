(ns bts-picker.data.games-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [bts-picker.data.games :as impl]
            [bts-picker.util.core :as date-util]))

(deftest get-games-test
  (testing "gets game ids for date"
    (is (= ["446277" (impl/get-games "2015-11-1")]))
    (is (= ["446277" (impl/get-games (date-util/of 2015 11 1))]))))

(deftest get-game-test
  (testing "gets game data"
    (is (not (nil? (impl/get-game "446277")))))) ; TODO
