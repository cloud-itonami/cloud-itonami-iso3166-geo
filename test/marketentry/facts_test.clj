(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest geo-has-spec-basis
  (let [sb (facts/spec-basis "GEO")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "GEO")))
    (is (some? (facts/black-list-spec-basis "GEO")))
    (is (some? (facts/white-list-spec-basis "GEO")))))

(deftest geo-has-no-rep-spec-basis
  (testing "no bidder's-representative disqualification clause was found in Georgian law -- honestly nil, not fabricated"
    (is (nil? (facts/rep-spec-basis "GEO")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "GEO")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "GEO" all)))
    (is (not (facts/required-evidence-satisfied? "GEO" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["GEO" "ATL" "ZZZ"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "ZZZ"] (:missing-jurisdictions c)))))
