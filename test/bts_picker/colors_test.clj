(ns bts-picker.colors-test
  (:require [clojure.test :refer :all]
            [bts-picker.colors :as impl]))

(deftest colors-test
  (testing "gets colors"
    (let [colors (impl/team-colors "Kansas City Royals")]
      (is (not (nil? colors))))))
