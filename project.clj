(defproject fixurls "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                  [clj-http "0.9.2"]
                  [org.clojure/data.json "0.2.4"]
                  ]
  :plugins [[lein-gorilla "0.2.0"]]
  :injections [(require 'clojure.pprint)]
  )
