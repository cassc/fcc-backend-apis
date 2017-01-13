(defproject backend-apis "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [hiccup "1.0.5"]
                 [cheshire "5.6.3"]
                 [http-kit "2.2.0"]
                 [compojure "1.5.1" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/tools.reader "1.0.0-beta3"]
                 [cassc/clj-props "0.1.2"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [buddy "1.1.0"]
                 [org.clojure/core.memoize "0.5.9"]
                 
                 [dk.ative/docjure "1.10.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [com.zaxxer/HikariCP "2.3.8"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [org.clojure/core.async "0.2.385"]
                 [environ "1.1.0"]]
  :plugins [[lein-environ "1.1.0"]]
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :profiles {:dev {:env {:dev true}
                   :source-paths ["src-dev"]}}
  :main ^:skip-aot backend-apis.core)
