(defproject bts-picker "0.1.0"
  :description "A tool to make MLB Beat the Streak picks for me"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [clj-http "3.4.1"]
                 [org.clojure/data.csv "0.1.3"]
                 [reaver "0.1.2"]
                 [clojure.java-time "0.2.2"]
                 [org.clojure/tools.trace "0.7.9"]
                 [cheshire "5.7.0"]]
  :main bts-picker.core)
