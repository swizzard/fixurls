(ns fixurls.core
  (:require
    [clj-http.client :as client]
    [clojure.string :as string]
    [clojure.java.io :as io]
    [me.raynes.fs :as fs]
    [clojure.data.json :as json]
    )
    (:import [java.net URL])
    (:gen-class)
  )

(def directory (let [pcp-ts (fs/expand-home "~/PycharmProjects/tweet_stuff")]
                  (if (fs/exists? pcp-ts)
                    (str pcp-ts "/extracted2")
                    (fs/expand-home "~/tweet_stuff/extracted2"))))

(def fixed-directory (let [fixed-dir (string/join "/"
                                      (conj (pop (string/split (str directory)
                                                  #"/"))
                                        "fixed"))]
                      (do
                        (or
                          (fs/exists? fixed-dir)
                          (fs/mkdir fixed-dir))
                        fixed-dir)))

(defn file-is-valid [fname] (let [file-name (str (fs/name fname) ".json")]
                              (or (nil? (re-matches #"\w+.json" file-name))
                                  (fs/exists?
                                    (str fixed-directory "/" file-name)))))

(def valid-files (map #(str directory "/" %)
                  (remove file-is-valid (fs/list-dir directory))))

(defn get-lines [f] (string/split (slurp f) #"\n"))

(defn parse-file [f] (map #(json/read-str %) (get-lines f)))

(defn expand-urls [urls] (vec (doall (for [url-str urls]
                           (and url-str
                               (try (last (:trace-redirects
                                           (client/get url-str)))
                                (catch Exception e
                                  (str (first (string/split url-str #"//")) "//"
                                    (first (string/split (.getMessage e) #":"))
                                  ))))))))

(defn get-with-urls [f] (filter #(not= [] (get-in % [1 0 "urls"]))
                          (parse-file f)))

(def ^:dynamic *domain-pat* (re-pattern #"https?://([\w\.]+)/.*"))

(defn get-domains [urls] (vec (for [url urls] (.getHost (URL. url)))))

(defn update-urls [js-line] (update-in js-line [1 0 "urls"] expand-urls))

(defn update-domains [js-line] (assoc-in js-line [1 0 "domains"]
                                (get-domains (get-in js-line [1 0 "urls"]))))

(defn update-both [js-line] (let [fixed (update-domains (update-urls js-line))]
                                  fixed))

(defn get-fixed-name [fname] (let [splt (string/split (str fname) #"/")]
                              (str fixed-directory "/" (last splt))))

; (defn fix-all-urls [lines] (let [fixed (map update-urls lines)] fixed))
;
; (defn fix-all-domains [lines] (let [fixed (map update-domains lines)] fixed))
;
; (defn update-file [in-file] (let [lines (parse-file in-file)]
;                                   (let [urls-fixed (fix-all-urls lines)]
;                                     (let [fixed (fix-all-domains urls-fixed)]
;                                   (string/join "\n" fixed)))))

(defn update-file [in-file] (let [lines (parse-file in-file)]
                              (doall (map update-both lines))))


(defn process-file [in-file] (do (println (str in-file))
                              (spit (get-fixed-name in-file)
                                (string/join "\n" (update-file in-file)))))

(defn process-files [] (dorun (pmap process-file valid-files)))

(defn -main [] (do (println "Hello, world!") (process-files)))
