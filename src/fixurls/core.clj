(ns fixurls.core
  (:require
    [clj-http.client :as client]
    [clojure.string :as string]
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    )
    (:import [java.net URL])
  )

(def ^:dynamic *directory*
  "/Users/samuelraker/PycharmProjects/tweet_stuff/extracted2")

(def valid-files (filter #(not (nil? (re-matches #"\w+.json"
                                      (last (string/split (str %) #"/")))))
                  (file-seq (io/file *directory*))))

(defn get-lines [f] (with-open [fil (io/reader f)]
                      (doall (line-seq fil))))

(defn parse-file [f] (map #(json/read-str %) (get-lines f)))

(defn expand-urls [urls] (vec (doall (for [url-str urls]
                           (and url-str (last (:trace-redirects
                                              (client/get url-str))))))))

(defn get-with-urls [f] (filter #(not= [] (get-in %1 [1 0 "urls"]))
                          (parse-file f)))

(def ^:dynamic *domain-pat* (re-pattern #"https?://([\w\.]+)/.*"))

(defn get-domains [urls] (vec (for [url urls] (.getHost (URL. url)))))

(defn update-urls [js-line] (update-in js-line [1 0 "urls"] expand-urls))

(defn update-domains [js-line] (assoc-in js-line [1 0 "domains"]
                                (get-domains (get-in js-line [1 0 "urls"]))))

(defn update-both [js-line] (update-domains (update-urls js-line)))
