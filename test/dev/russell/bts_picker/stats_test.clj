(ns dev.russell.bts-picker.stats-test
  (:require [clojure.test :refer :all]
            [dev.russell.bts-picker.stats :as impl]))

(deftest range-normalized-score-test
  (testing "normalizes scores"
    (let [score-set [{:score 0.9}
                     {:score 0.71}
                     {:score 0.1}
                     {:score 0.66}
                     {:score 0.25}]]
      (is (= (impl/range-normalized-score score-set)
             [{:score 0.9 :normalized-score 1.0}
              {:score 0.71 :normalized-score 0.7625}
              {:score 0.1 :normalized-score 0.0}
              {:score 0.66 :normalized-score 0.7000000000000001}
              {:score 0.25 :normalized-score 0.18749999999999997}])))))
