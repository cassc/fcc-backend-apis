(ns backend-apis.routes.url-shorten
  (:require
   [backend-apis.config :refer [props]]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [ring.util.response       :refer [response redirect]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all]))

(defonce db
  (do
    (when-not (.exists (io/file "short-urls.txt"))
      (spit (io/file "short-urls.txt") {}))
    (atom (read-string (slurp "short-urls.txt")))))

(defn save-code [code url]
  (swap! db assoc code url)
  (spit (io/file "short-urls.txt") @db))

(defn code->url [code]
  (@db code))

(defn url->code [url]
  (some (fn [[code val]] (when (= val url) code)) @db))

(defn rand-code []
  (let [s "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"]
    (reduce str (repeatedly 4 #(rand-nth s)))))

(defn valid-url? [url]
  (re-seq (props :valid-url-regex) url))

(defn shorten-url [fullurl allow]
  (if-let [code (url->code fullurl)]
    (response
     {:original_url fullurl
      :short_url (str (props :base-url) "/s/" code)})
    (if-let [code (and
                   (or allow (valid-url? fullurl))
                   (rand-code))]
      (if (@db code)
        (recur fullurl allow)
        (do
          (save-code code fullurl)
          (response
           {:original_url fullurl
            :short_url (str (props :base-url) "/s/" code)})))
      (response
       {:invalid true
        :original_url fullurl}))))

(defn redirect-url [code]
  (redirect (@db code)))

(defroutes url-shorten-routes
  (GET ["/short/:fullurl" :fullurl #".*"] [fullurl allow] (shorten-url fullurl allow))
  (GET ["/s/:code" :code #".*"] [code] (redirect-url code)))

