(ns bts-picker.games-test
  (:require [clojure.test :refer :all]
            [bts-picker.games :as impl]))

(deftest games-test
  (testing "gets games"
    (let [games (impl/games)]
      (is (not (nil? games))))))
