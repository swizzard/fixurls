(defproject fixurls "0.1.0-SNAPSHOT"
  :description "Fixurls"
  :url "https://github.com/swizzard/fixurls"
  :license {:name "WTFPL"
            :url "http://www.wtfpl.net/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                  [clj-http "0.9.2"]
                  [org.clojure/data.json "0.2.4"]
                  [me.raynes/fs "1.4.4"]
                  ]
  :plugins [[lein-gorilla "0.2.0"]]
  ; :jvm-opts ["-Xmx4g" "-Xms2g" "-Xss1g" "-server"]
  :main ^:skip-aot fixurls.core
  )
