(def VERSION (.trim (slurp "VERSION")))

(defproject dev.russell/beat-the-streak-beater VERSION
  :description "A tool to make MLB Beat the Streak picks"
  :url "https://russell.dev/beat-the-streak-beater"
  :license {:name "apache-2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [dev.russell/batboy "0.3.0"]
                 [com.github.seancorfield/next.jdbc "1.2.780"]
                 [org.postgresql/postgresql "42.3.4"]
                 [migratus "1.3.6"]
                 [clojure.java-time "1.4.2"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.trace "0.8.0"]
                 [com.taoensso/timbre "5.2.1"]
                 [org.clojure/test.check "1.1.1"]
                 [com.gfredericks/test.chuck "0.2.13"]]
  :plugins [[migratus-lein "0.7.3"]]
  :profiles {:dev [:project/dev :profiles/dev]
             :test [:project/test :profiles/test]
             :dev-ops [:project/dev-ops :profiles/dev-ops]
             :profiles/dev  {}
             :profiles/test {}
             :profiles/dev-ops {}
             :project/dev {}
             :project/test {}
             :project/dev-ops {:source-paths ["db/"]}}
  :main dev.russell.bts-beater.core
  :aliases {"launch" ["run" "-m" "dev.russell.bts-beater.core"]}
  :repl-options {:init-ns dev.russell.bts-beater.core}
  :deploy-branches ["master"]
  :migratus {:store :database
             :migration-dir "db/migrations/"
             :init-script "init.edn"
             :migration-table-name "migrations"
             :db {:dbtype "postgresql"
                  :dbname #=(eval (if (= (System/getenv "BTS_ENV") "test") "bts_test" "bts"))
                  :user #=(eval (System/getenv "BTS_DB_USER"))
                  :password #=(eval (System/getenv "BTS_DB_PASSWORD"))}})
