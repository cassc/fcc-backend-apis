(ns backend-apis.routes.image-search
  (:require
   [backend-apis.config :refer [props]]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [ring.util.response       :refer [response redirect]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all]))

(defn img-search [{:keys [term offset] :as params}]
  (response params))

(defn latest-img-search []
  (response
   {:not-found 1}))

(defroutes img-search-routes
  (GET "/img/search" [& params] (img-search params))
  (GET "/img/latestsearch" [] (latest-img-search)))

