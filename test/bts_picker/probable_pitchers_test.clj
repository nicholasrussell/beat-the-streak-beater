(ns bts-picker.probable-pitchers-test
  (:require [clojure.test :refer :all]
            [bts-picker.probable-pitchers :as impl]))

(deftest probable-pitcher-pids-test
  (testing "gets pids"
    (let [probable-pitchers (impl/probable-pitchers)]
      (is (not (nil? probable-pitchers))))))
