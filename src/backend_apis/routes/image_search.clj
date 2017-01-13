(ns backend-apis.routes.image-search
  "Using google image search API: 
  http://stackoverflow.com/questions/34035422/google-image-search-says-api-no-longer-available"
  (:require
   [cheshire.core :as cheshire :refer [parse-string]]
   [org.httpkit.client :as client]
   [environ.core :refer [env]]
   [backend-apis.config :refer [props]]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [ring.util.response       :refer [response redirect]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all]))

(def google-api-key (delay (env :google-api-key)))
(def google-cx (delay (env :google-cx)))

(defonce db
  (do
    (when-not (.exists (io/file "latestsearch.txt"))
      (spit (io/file "latestsearch.txt") []))
    (atom (read-string (slurp "latestsearch.txt")))))

(defn add-to-db [term]
  (swap! db conj term)
  (spit (io/file "latestsearch.txt") @db))

(defn make-img-search-url [{:keys [term offset]}]
  (when-not (and @google-api-key @google-cx)
    (throw (RuntimeException.
            (str "你还没有配置Google API key(google-api-key)及Search engine ID(google-cx)。参考"
                 "http://stackoverflow.com/questions/34035422/google-image-search-says-api-no-longer-available"))))
  (str "https://www.googleapis.com/customsearch/v1?"
       "q=" term
       "&num=10"
       (when offset (str "&start=" offset))
       "&searchType=image&key=" @google-api-key
       "&cx=" @google-cx))

(defn goog-resp->list [{:keys [items]}]
  (map
   (fn [{:keys [link snippet image title]}]
     {:url link
      :snippet snippet
      :thumbnail (:thumbnailLink image)
      :context (:contextLink image)
      :title title})
   items))

(defn img-search [{:keys [term offset] :as params}]
  (let [{:keys [status error body]} @(client/get (make-img-search-url params))]
    (add-to-db term)
    (if (or error (not= 200 status))
      (response {:error error :body body})
      (response (-> body
                    (parse-string keyword)
                    goog-resp->list)))))

(defn latest-img-search []
  (response
   (take 10 (reverse @db))))

(defroutes img-search-routes
  (GET "/img/search" [& params] (img-search params))
  (GET "/img/latestsearch" [] (latest-img-search)))

