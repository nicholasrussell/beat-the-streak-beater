(defproject bts-picker "0.1.0"
  :description "A tool to make MLB Beat the Streak picks for me"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.0"]
                 [org.clojure/data.csv "1.0.0"]
                 [reaver "0.1.3"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/tools.trace "0.7.10"]
                 [cheshire "5.10.0"]
                 [com.rpl/specter "1.1.3"]
                 [org.clojure/test.check "1.0.0"]
                 [com.gfredericks/test.chuck "0.2.10"]]
  :managed-dependencies [[org.clojure/core.rrb-vector "0.1.1"]]
  :main bts-picker.core
  :profiles {:repl {:plugins [[cider/cider-nrepl "0.22.0"]
                              [refactor-nrepl "2.4.0"]]
                    :dependencies [[org.clojure/tools.nrepl "0.2.13"]
                                   [alembic "0.3.2"]]}})

