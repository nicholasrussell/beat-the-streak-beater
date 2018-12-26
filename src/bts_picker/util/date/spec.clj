(ns bts-picker.util.date.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as tgen]
            [bts-picker.util.date.core :as impl])
  (:import (java.time LocalDate YearMonth)
           (java.time.format DateTimeFormatter)))

(def ^:private iso-date-regex #"\d{4}-\d{2}-\d{2}")

(defn- max-days-in-month
  [year month]
  (.lengthOfMonth (YearMonth/of (#'impl/->int year) (#'impl/->int month))))

(def local-date-parts-generator
  (tgen/fmap
   (fn [[year month day]]
     (let [gen-year (-> year (mod 3000) inc)
           gen-month (-> month (mod 12) inc)
           gen-day-max (max-days-in-month gen-year gen-month)
           gen-day (-> day (mod gen-day-max) inc)
           string-or-int (fn [i] (if (= (rand-int 2) 1) (str i) i))]
       [(-> gen-year string-or-int)
        (-> gen-month string-or-int)
        (-> gen-day string-or-int)]))
   (tgen/vector tgen/pos-int 3)))

(def iso-date-string-generator
  (tgen/fmap
   (fn [[year month day]]
     (let [local-date (impl/of year month day)]
       (.format local-date DateTimeFormatter/ISO_LOCAL_DATE)))
   local-date-parts-generator))

(def local-date-generator
  (tgen/fmap
   (fn [[year month day]]
     (impl/of year month day))
   local-date-parts-generator))

(s/def ::iso-date-string (s/with-gen
                          (s/and string?
                                 #(re-matches iso-date-regex %)
                                 #(LocalDate/parse % DateTimeFormatter/ISO_LOCAL_DATE))
                          (fn [] iso-date-string-generator)))
(s/def ::local-date (s/with-gen
                     #(instance? LocalDate %)
                     (fn [] local-date-generator)))
(s/def ::iso-date (s/or :iso-date-string ::iso-date-string
                        :local-date ::local-date))
(s/def ::local-date-part (s/or :integer integer?
                               :string string?))

(s/fdef impl/now
        :args (s/cat)
        :ret ::local-date)

(s/fdef impl/of
        :args (s/with-gen
               (s/cat :year ::local-date-part
                      :month ::local-date-part
                      :day ::local-date-part)
               (fn [] local-date-parts-generator))
        :ret ::local-date)

(s/fdef impl/format-date
        :args (s/cat :date ::iso-date)
        :ret ::iso-date-string)
